"""
Pytest configuration: stubs out generated protobuf modules so tests run
without requiring the proto generation step.
"""
import sys
import os
from unittest.mock import MagicMock

sys.path.insert(0, os.path.join(os.path.dirname(__file__), "..", "src"))

_NOTIFICATION_STATE_NAMES = {
    0: "NOTIFICATION_STATE_UNSPECIFIED",
    1: "NOTIFICATION_STATE_PENDING",
    2: "NOTIFICATION_STATE_DELIVERED",
    3: "NOTIFICATION_STATE_ACKNOWLEDGED",
    4: "NOTIFICATION_STATE_FAILED",
}

mock_notification_pb2 = MagicMock()
mock_notification_pb2.NotificationState.Name.side_effect = (
    lambda x: _NOTIFICATION_STATE_NAMES.get(x, "NOTIFICATION_STATE_UNSPECIFIED")
)

mock_analytics_pb2 = MagicMock()
mock_analytics_pb2_grpc = MagicMock()

for _mod in ("notification", "notification.v1"):
    sys.modules[_mod] = MagicMock()
sys.modules["notification.v1.notification_pb2"] = mock_notification_pb2

for _mod in ("analytics", "analytics.v1"):
    sys.modules[_mod] = MagicMock()
sys.modules["analytics.v1.analytics_pb2"] = mock_analytics_pb2
sys.modules["analytics.v1.analytics_pb2_grpc"] = mock_analytics_pb2_grpc
