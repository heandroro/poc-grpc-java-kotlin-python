"""Unit tests for AnalyticsServiceImpl gRPC methods."""
from unittest.mock import MagicMock, patch
import grpc
import pytest

import analytics_service as svc


@pytest.fixture(autouse=True)
def fresh_store(monkeypatch):
    """Replace the module-level _store with a clean instance for each test."""
    new_store = svc.EventStore()
    monkeypatch.setattr(svc, "_store", new_store)
    return new_store


@pytest.fixture()
def service():
    return svc.AnalyticsServiceImpl()


@pytest.fixture()
def context():
    ctx = MagicMock()
    ctx.is_active.return_value = True
    return ctx


def _mock_request(filter_type=None, filter_value=None):
    req = MagicMock()
    req.WhichOneof.return_value = filter_type
    if filter_type == "topic":
        req.topic = filter_value
    elif filter_type == "user_id":
        req.user_id = filter_value
    return req


class TestGetStats:
    def test_should_return_zero_stats_for_empty_topic(self, service, context):
        request = _mock_request("topic", "promos")
        response = service.GetStats(request, context)

        assert response.total_sent == 0
        assert response.total_delivered == 0
        assert response.delivery_rate == 0.0

    def test_should_aggregate_topic_events(self, service, context, fresh_store):
        fresh_store.record("n-001", "user-001", "promos", 2)
        fresh_store.record("n-002", "user-001", "promos", 2)
        fresh_store.record("n-003", "user-001", "promos", 4)

        request = _mock_request("topic", "promos")
        response = service.GetStats(request, context)

        assert response.total_delivered == 2
        assert response.total_failed == 1

    def test_should_calculate_delivery_rate(self, service, context, fresh_store):
        fresh_store.record("n-001", "user-001", "promos", 1)
        fresh_store.record("n-002", "user-001", "promos", 2)

        request = _mock_request("topic", "promos")
        response = service.GetStats(request, context)

        assert response.delivery_rate == pytest.approx(0.5)

    def test_should_aggregate_user_events(self, service, context, fresh_store):
        fresh_store.record("n-001", "user-001", "promos", 3)

        request = _mock_request("user_id", "user-001")
        response = service.GetStats(request, context)

        assert response.total_acknowledged == 1

    def test_should_abort_when_no_filter_provided(self, service, context):
        request = _mock_request(None)
        service.GetStats(request, context)

        context.abort.assert_called_once_with(grpc.StatusCode.INVALID_ARGUMENT, "filter (user_id or topic) is required")

    def test_should_return_zero_delivery_rate_when_no_sent(self, service, context):
        request = _mock_request("topic", "empty-topic")
        response = service.GetStats(request, context)
        assert response.delivery_rate == 0.0


class TestRecordEvent:
    def _valid_request(self, notification_id="n-001", user_id="user-001", topic="promos", state=2):
        req = MagicMock()
        req.notification_id = notification_id
        req.user_id = user_id
        req.topic = topic
        req.state = state
        return req

    def test_should_record_event_and_return_true(self, service, context):
        request = self._valid_request()
        response = service.RecordEvent(request, context)

        assert response.recorded is True

    def test_should_abort_when_notification_id_is_empty(self, service, context):
        request = self._valid_request(notification_id="")
        service.RecordEvent(request, context)

        context.abort.assert_called_once_with(
            grpc.StatusCode.INVALID_ARGUMENT,
            "notification_id, user_id and topic are required",
        )

    def test_should_abort_when_user_id_is_empty(self, service, context):
        request = self._valid_request(user_id="")
        service.RecordEvent(request, context)

        context.abort.assert_called_once()

    def test_should_abort_when_topic_is_empty(self, service, context):
        request = self._valid_request(topic="")
        service.RecordEvent(request, context)

        context.abort.assert_called_once()

    def test_should_persist_event_in_store(self, service, context, fresh_store):
        request = self._valid_request(state=2)
        service.RecordEvent(request, context)

        counts = fresh_store.get_stats_by_topic("promos")
        assert counts.get("NOTIFICATION_STATE_DELIVERED") == 1


class TestStreamMetrics:
    def test_should_yield_snapshot_and_stop_when_context_cancelled(self, service, context, fresh_store, monkeypatch):
        fresh_store.record("n-001", "user-001", "promos", 2)

        call_count = 0

        def is_active_side_effect():
            nonlocal call_count
            call_count += 1
            return call_count <= 1

        context.is_active.side_effect = is_active_side_effect

        req = MagicMock()
        req.interval_seconds = 0
        req.topics = ["promos"]

        monkeypatch.setattr(svc.time, "sleep", lambda _: None)

        results = list(service.StreamMetrics(req, context))

        assert len(results) >= 1

    def test_should_use_all_topics_when_no_filter(self, service, context, fresh_store, monkeypatch):
        fresh_store.record("n-001", "user-001", "promos", 2)

        call_count = 0

        def is_active_side_effect():
            nonlocal call_count
            call_count += 1
            return call_count <= 1

        context.is_active.side_effect = is_active_side_effect

        req = MagicMock()
        req.interval_seconds = 0
        req.topics = []

        monkeypatch.setattr(svc.time, "sleep", lambda _: None)

        results = list(service.StreamMetrics(req, context))
        assert len(results) >= 1
