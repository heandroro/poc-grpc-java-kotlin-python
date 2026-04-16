"""
Best Practice: Analytics gRPC service demonstrating server-streaming, Prometheus metrics,
OpenTelemetry tracing and structured logging in Python.
"""
from __future__ import annotations

import time
import threading
from collections import defaultdict
from datetime import datetime, timezone

import grpc
import structlog
from google.protobuf.timestamp_pb2 import Timestamp
from prometheus_client import Counter, Histogram, start_http_server

import sys
import os
sys.path.insert(0, os.path.join(os.path.dirname(__file__), "generated"))

from analytics.v1 import analytics_pb2, analytics_pb2_grpc
from notification.v1 import notification_pb2

log = structlog.get_logger(__name__)

# ---------------------------------------------------------------------------
# Prometheus metrics
# ---------------------------------------------------------------------------
EVENTS_TOTAL = Counter(
    "analytics_events_total",
    "Total notification events recorded",
    ["topic", "state"],
)
RPC_LATENCY = Histogram(
    "analytics_rpc_duration_seconds",
    "gRPC handler latency",
    ["method"],
)

# ---------------------------------------------------------------------------
# In-memory event store
# ---------------------------------------------------------------------------

class EventStore:
    def __init__(self):
        self._lock = threading.Lock()
        # topic -> state -> count
        self._counts: dict[str, dict[str, int]] = defaultdict(lambda: defaultdict(int))
        # user -> state -> count
        self._user_counts: dict[str, dict[str, int]] = defaultdict(lambda: defaultdict(int))

    def record(self, notification_id: str, user_id: str, topic: str, state: int):
        state_name = notification_pb2.NotificationState.Name(state)
        with self._lock:
            self._counts[topic][state_name] += 1
            self._user_counts[user_id][state_name] += 1
        EVENTS_TOTAL.labels(topic=topic, state=state_name).inc()
        log.info("event_recorded", notification_id=notification_id, user_id=user_id, topic=topic, state=state_name)

    def get_stats_by_topic(self, topic: str) -> dict[str, int]:
        with self._lock:
            return dict(self._counts.get(topic, {}))

    def get_stats_by_user(self, user_id: str) -> dict[str, int]:
        with self._lock:
            return dict(self._user_counts.get(user_id, {}))

    def get_snapshot(self, topic: str) -> dict[str, int]:
        with self._lock:
            return dict(self._counts.get(topic, {}))

    def all_topics(self) -> list[str]:
        with self._lock:
            return list(self._counts.keys())


_store = EventStore()


# ---------------------------------------------------------------------------
# Service implementation
# ---------------------------------------------------------------------------

class AnalyticsServiceImpl(analytics_pb2_grpc.AnalyticsServiceServicer):

    def GetStats(self, request, context):
        """Unary — returns aggregated stats for a user or topic."""
        start = time.monotonic()
        try:
            which = request.WhichOneof("filter")
            if which == "topic":
                counts = _store.get_stats_by_topic(request.topic)
                label = f"topic={request.topic}"
            elif which == "user_id":
                counts = _store.get_stats_by_user(request.user_id)
                label = f"user_id={request.user_id}"
            else:
                context.abort(grpc.StatusCode.INVALID_ARGUMENT, "filter (user_id or topic) is required")
                return

            sent = counts.get("NOTIFICATION_STATE_PENDING", 0) + counts.get("NOTIFICATION_STATE_DELIVERED", 0)
            delivered = counts.get("NOTIFICATION_STATE_DELIVERED", 0)
            acked = counts.get("NOTIFICATION_STATE_ACKNOWLEDGED", 0)
            failed = counts.get("NOTIFICATION_STATE_FAILED", 0)

            delivery_rate = (delivered / sent) if sent > 0 else 0.0
            ack_rate = (acked / max(delivered, 1))

            log.info("get_stats", filter=label, sent=sent, delivered=delivered)
            return analytics_pb2.GetStatsResponse(
                total_sent=sent,
                total_delivered=delivered,
                total_acknowledged=acked,
                total_failed=failed,
                delivery_rate=delivery_rate,
                ack_rate=ack_rate,
            )
        finally:
            RPC_LATENCY.labels(method="GetStats").observe(time.monotonic() - start)

    def StreamMetrics(self, request, context):
        """
        Server streaming — emits metric snapshots at a configurable interval.
        Best Practice: Check context.is_active() to detect client cancellation cleanly.
        """
        interval = max(request.interval_seconds, 1)
        topics = list(request.topics) if request.topics else None

        log.info("stream_metrics_started", interval_s=interval, topics=topics)

        while context.is_active():
            target_topics = topics if topics else _store.all_topics()
            for topic in target_topics:
                if not context.is_active():
                    break
                snap = _store.get_snapshot(topic)
                ts = _now_timestamp()
                yield analytics_pb2.MetricSnapshot(
                    topic=topic,
                    sent_last_interval=snap.get("NOTIFICATION_STATE_PENDING", 0),
                    delivered_last_interval=snap.get("NOTIFICATION_STATE_DELIVERED", 0),
                    failed_last_interval=snap.get("NOTIFICATION_STATE_FAILED", 0),
                    snapshot_at=ts,
                )
            time.sleep(interval)

        log.info("stream_metrics_ended")

    def RecordEvent(self, request, context):
        """Unary — ingest a notification lifecycle event."""
        start = time.monotonic()
        try:
            if not request.notification_id or not request.user_id or not request.topic:
                context.abort(grpc.StatusCode.INVALID_ARGUMENT, "notification_id, user_id and topic are required")
                return

            _store.record(
                notification_id=request.notification_id,
                user_id=request.user_id,
                topic=request.topic,
                state=request.state,
            )
            return analytics_pb2.RecordEventResponse(recorded=True)
        finally:
            RPC_LATENCY.labels(method="RecordEvent").observe(time.monotonic() - start)


def _now_timestamp() -> Timestamp:
    now = datetime.now(tz=timezone.utc)
    ts = Timestamp()
    ts.FromDatetime(now)
    return ts
