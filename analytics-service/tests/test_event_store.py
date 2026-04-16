"""Unit tests for EventStore — pure Python, no proto stubs needed."""
import pytest

import analytics_service as svc


@pytest.fixture()
def store():
    return svc.EventStore()


def test_record_increments_topic_counts(store):
    store.record("n-001", "user-001", "promos", 2)

    counts = store.get_stats_by_topic("promos")
    assert counts.get("NOTIFICATION_STATE_DELIVERED") == 1


def test_record_increments_user_counts(store):
    store.record("n-001", "user-001", "promos", 1)

    counts = store.get_stats_by_user("user-001")
    assert counts.get("NOTIFICATION_STATE_PENDING") == 1


def test_multiple_records_accumulate(store):
    store.record("n-001", "user-001", "promos", 2)
    store.record("n-002", "user-001", "promos", 2)
    store.record("n-003", "user-001", "promos", 3)

    topic_counts = store.get_stats_by_topic("promos")
    assert topic_counts.get("NOTIFICATION_STATE_DELIVERED") == 2
    assert topic_counts.get("NOTIFICATION_STATE_ACKNOWLEDGED") == 1


def test_get_stats_by_topic_returns_empty_for_unknown_topic(store):
    result = store.get_stats_by_topic("nonexistent")
    assert result == {}


def test_get_stats_by_user_returns_empty_for_unknown_user(store):
    result = store.get_stats_by_user("ghost")
    assert result == {}


def test_get_snapshot_returns_topic_counts(store):
    store.record("n-001", "user-001", "promos", 2)

    snap = store.get_snapshot("promos")
    assert snap.get("NOTIFICATION_STATE_DELIVERED") == 1


def test_get_snapshot_returns_empty_for_unknown_topic(store):
    assert store.get_snapshot("unknown") == {}


def test_all_topics_returns_known_topics(store):
    store.record("n-001", "user-001", "promos", 2)
    store.record("n-002", "user-002", "alerts", 1)

    topics = store.all_topics()
    assert "promos" in topics
    assert "alerts" in topics


def test_all_topics_returns_empty_when_no_events(store):
    assert store.all_topics() == []


def test_record_returns_independent_copies(store):
    store.record("n-001", "user-001", "promos", 2)

    copy1 = store.get_stats_by_topic("promos")
    copy2 = store.get_stats_by_topic("promos")
    copy1["NOTIFICATION_STATE_DELIVERED"] = 999

    assert copy2.get("NOTIFICATION_STATE_DELIVERED") == 1
