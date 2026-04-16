"""
End-to-end integration tests covering all three gRPC services.

Test order follows the real user flows documented in the root README:
  1. SendNotification       — unary RPC on notification-service
  2. Subscribe + List       — subscription-service state management
  3. PublishToSubscribers   — fan-out: subscription-service → notification-service
  4. StreamNotifications    — server streaming on notification-service
  5. RecordEvent + GetStats — analytics-service ingest + query
"""
from __future__ import annotations

import queue
import threading
import time

import grpc
import pytest


# ──────────────────────────────────────────────────────────────────────────────
# 1. notification-service — SendNotification (unary)
# ──────────────────────────────────────────────────────────────────────────────
@pytest.mark.timeout(30)
def test_send_notification_unary(stubs, jwt_meta, pb2):
    n_pb2 = pb2["n_pb2"]
    req = n_pb2.SendNotificationRequest(
        user_id="user-e2e-001",
        topic="promos",
        title="Oferta especial",
        body="50% de desconto hoje",
        priority=n_pb2.NOTIFICATION_PRIORITY_HIGH,
    )
    resp = stubs.ns.SendNotification(req, metadata=jwt_meta, timeout=10)

    assert resp.notification_id != "", "notification_id deve ser preenchido"
    assert resp.HasField("accepted_at"), "accepted_at deve estar presente"


# ──────────────────────────────────────────────────────────────────────────────
# 2. subscription-service — Subscribe + ListSubscriptions
# ──────────────────────────────────────────────────────────────────────────────
@pytest.mark.timeout(30)
def test_subscribe_and_list_subscriptions(stubs, jwt_meta, pb2):
    n_pb2 = pb2["n_pb2"]
    s_pb2 = pb2["s_pb2"]

    sub_resp = stubs.ss.Subscribe(
        s_pb2.SubscribeRequest(
            user_id="user-e2e-002",
            topic="news",
            min_priority=n_pb2.NOTIFICATION_PRIORITY_LOW,
        ),
        metadata=jwt_meta,
        timeout=10,
    )
    assert sub_resp.subscription_id != "", "subscription_id deve ser preenchido"
    assert sub_resp.HasField("subscribed_at"), "subscribed_at deve estar presente"

    subscriptions = list(
        stubs.ss.ListSubscriptions(
            s_pb2.ListSubscriptionsRequest(user_id="user-e2e-002", active_only=True),
            metadata=jwt_meta,
            timeout=10,
        )
    )
    assert any(s.topic == "news" and s.active for s in subscriptions), (
        "Inscrição em 'news' deve aparecer em ListSubscriptions"
    )


# ──────────────────────────────────────────────────────────────────────────────
# 3. subscription-service → notification-service — PublishToSubscribers (fan-out)
# ──────────────────────────────────────────────────────────────────────────────
@pytest.mark.timeout(60)
def test_publish_fanout_reaches_subscribers(stubs, jwt_meta, pb2):
    n_pb2 = pb2["n_pb2"]
    s_pb2 = pb2["s_pb2"]
    topic = "flash-sale"

    for user_id in ("user-fanout-a", "user-fanout-b"):
        stubs.ss.Subscribe(
            s_pb2.SubscribeRequest(
                user_id=user_id,
                topic=topic,
                min_priority=n_pb2.NOTIFICATION_PRIORITY_LOW,
            ),
            metadata=jwt_meta,
            timeout=10,
        )

    publish_resp = stubs.ss.PublishToSubscribers(
        s_pb2.PublishToSubscribersRequest(
            topic=topic,
            title="Flash Sale",
            body="Apenas hoje!",
            priority=n_pb2.NOTIFICATION_PRIORITY_CRITICAL,
        ),
        metadata=jwt_meta,
        timeout=30,
    )

    assert publish_resp.notifications_sent == 2, (
        f"Esperado 2 notificações enviadas, recebido {publish_resp.notifications_sent}"
    )
    assert len(publish_resp.failed_user_ids) == 0, (
        f"Nenhuma falha esperada: {publish_resp.failed_user_ids}"
    )


# ──────────────────────────────────────────────────────────────────────────────
# 4. notification-service — StreamNotifications (server streaming)
# ──────────────────────────────────────────────────────────────────────────────
@pytest.mark.timeout(45)
def test_stream_notifications_receives_live_notification(stubs, jwt_meta, pb2):
    n_pb2 = pb2["n_pb2"]
    received: queue.Queue = queue.Queue()
    stream_error: list[Exception] = []

    def _stream():
        try:
            req = n_pb2.StreamNotificationsRequest(
                user_id="stream-e2e-user",
                min_priority=n_pb2.NOTIFICATION_PRIORITY_LOW,
            )
            for notif in stubs.ns.StreamNotifications(req, metadata=jwt_meta, timeout=20):
                received.put(notif)
                break
        except grpc.RpcError as exc:
            stream_error.append(exc)

    t = threading.Thread(target=_stream, daemon=True)
    t.start()
    time.sleep(1.5)

    stubs.ns.SendNotification(
        n_pb2.SendNotificationRequest(
            user_id="stream-e2e-user",
            topic="streaming-test",
            title="Mensagem em tempo real",
            body="Stream funcionando",
            priority=n_pb2.NOTIFICATION_PRIORITY_HIGH,
        ),
        metadata=jwt_meta,
        timeout=10,
    )

    notif = received.get(timeout=15)
    assert not stream_error, f"Stream encerrou com erro: {stream_error}"
    assert notif.user_id == "stream-e2e-user"
    assert notif.title == "Mensagem em tempo real"


# ──────────────────────────────────────────────────────────────────────────────
# 5. analytics-service — RecordEvent + GetStats
# ──────────────────────────────────────────────────────────────────────────────
@pytest.mark.timeout(30)
def test_analytics_record_event_and_get_stats(stubs, jwt_meta, pb2):
    n_pb2 = pb2["n_pb2"]
    a_pb2 = pb2["a_pb2"]
    topic = "analytics-e2e-topic"

    events = [
        ("n-e2e-1", n_pb2.NOTIFICATION_STATE_DELIVERED),
        ("n-e2e-2", n_pb2.NOTIFICATION_STATE_DELIVERED),
        ("n-e2e-3", n_pb2.NOTIFICATION_STATE_ACKNOWLEDGED),
        ("n-e2e-4", n_pb2.NOTIFICATION_STATE_FAILED),
    ]
    for notif_id, state in events:
        resp = stubs.as_.RecordEvent(
            a_pb2.RecordEventRequest(
                notification_id=notif_id,
                user_id="analytics-e2e-user",
                topic=topic,
                state=state,
            ),
            metadata=jwt_meta,
            timeout=10,
        )
        assert resp.recorded is True, f"RecordEvent falhou para {notif_id}"

    stats = stubs.as_.GetStats(
        a_pb2.GetStatsRequest(topic=topic),
        metadata=jwt_meta,
        timeout=10,
    )

    assert stats.total_delivered >= 2, (
        f"Esperado >= 2 entregues, recebido {stats.total_delivered}"
    )
    assert stats.total_acknowledged >= 1, (
        f"Esperado >= 1 confirmado, recebido {stats.total_acknowledged}"
    )
    assert stats.total_failed >= 1, (
        f"Esperado >= 1 falha, recebido {stats.total_failed}"
    )
    assert stats.delivery_rate > 0.0, "delivery_rate deve ser > 0"
