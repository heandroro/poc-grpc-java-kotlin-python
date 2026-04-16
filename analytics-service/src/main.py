"""
analytics-service entrypoint.
Best Practices demonstrated:
  - JWT interceptor for server-side auth
  - Structured logging via structlog
  - Prometheus metrics via prometheus_client
  - OpenTelemetry traces exported to Jaeger via OTLP
"""
from __future__ import annotations

import logging
import os
import signal
import sys
from concurrent import futures

import grpc
import structlog
from prometheus_client import start_http_server

import sys
sys.path.insert(0, os.path.join(os.path.dirname(__file__), "generated"))

from analytics.v1 import analytics_pb2_grpc
from analytics_service import AnalyticsServiceImpl
from interceptors import JwtServerInterceptor, LoggingServerInterceptor, configure as configure_jwt
from otel_setup import configure_otel

# ---------------------------------------------------------------------------
# Structured logging
# ---------------------------------------------------------------------------
structlog.configure(
    wrapper_class=structlog.make_filtering_bound_logger(logging.INFO),
    processors=[
        structlog.processors.TimeStamper(fmt="iso"),
        structlog.processors.add_log_level,
        structlog.processors.JSONRenderer(),
    ],
)
log = structlog.get_logger(__name__)

# ---------------------------------------------------------------------------
# Config from env
# ---------------------------------------------------------------------------
GRPC_PORT = int(os.getenv("GRPC_PORT", "50053"))
PROMETHEUS_PORT = int(os.getenv("PROMETHEUS_PORT", "9091"))
JWT_SECRET = os.getenv("JWT_SECRET", "poc-grpc-super-secret-key-change-in-production")
OTEL_ENDPOINT = os.getenv("OTEL_EXPORTER_OTLP_ENDPOINT", "http://jaeger:4317")
SERVICE_NAME = "analytics-service"


def serve():
    configure_jwt(JWT_SECRET)
    configure_otel(SERVICE_NAME, OTEL_ENDPOINT)

    start_http_server(PROMETHEUS_PORT)
    log.info("prometheus_started", port=PROMETHEUS_PORT)

    server = grpc.server(
        futures.ThreadPoolExecutor(max_workers=10),
        interceptors=[
            JwtServerInterceptor(),
            LoggingServerInterceptor(),
        ],
    )

    analytics_pb2_grpc.add_AnalyticsServiceServicer_to_server(AnalyticsServiceImpl(), server)
    server.add_insecure_port(f"[::]:{GRPC_PORT}")
    server.start()

    log.info("analytics_service_started", grpc_port=GRPC_PORT)

    def _shutdown(sig, frame):
        log.info("shutdown_signal_received", signal=sig)
        server.stop(grace=5)
        sys.exit(0)

    signal.signal(signal.SIGTERM, _shutdown)
    signal.signal(signal.SIGINT, _shutdown)

    server.wait_for_termination()


if __name__ == "__main__":
    serve()
