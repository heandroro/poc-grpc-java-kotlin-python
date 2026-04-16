"""
Integration-test fixtures.

Session lifecycle:
  1. built_images       — docker compose build (uses infra/ context — same as production)
  2. generated_stubs    — grpc_tools.protoc → integration-tests/generated/
  3. docker_network     — user-defined bridge network poc-integration-net
  4. notification_svc   — start container, wait /actuator/health
  5. subscription_svc   — start container (NOTIFICATION_SERVICE_HOST=notification-service),
                          wait /health
  6. analytics_svc      — start container, wait gRPC port ready
  7. stubs              — grpc channels + service stubs for all three services
"""
from __future__ import annotations

import datetime
import importlib
import subprocess
import sys
import time
from collections import namedtuple
from pathlib import Path

import docker
import grpc
import jwt
import pytest
import requests
from testcontainers.core.container import DockerContainer

# ──────────────────────────────────────────────────────────────────────────────
# Constants
# ──────────────────────────────────────────────────────────────────────────────
REPO_ROOT = Path(__file__).parent.parent
IT_DIR = Path(__file__).parent
GENERATED_DIR = IT_DIR / "generated"

JWT_SECRET = "poc-grpc-super-secret-key-change-in-production"
COMPOSE_PROJECT = "poc-test"
NETWORK_NAME = "poc-integration-net"

_OTEL_NOOP = "http://localhost:14317"

Stubs = namedtuple("Stubs", ["ns", "ss", "as_", "ns_ch", "ss_ch", "as_ch"])


# ──────────────────────────────────────────────────────────────────────────────
# Wait helpers
# ──────────────────────────────────────────────────────────────────────────────
def _wait_http(url: str, timeout: int = 120) -> None:
    deadline = time.time() + timeout
    last_exc: Exception | None = None
    while time.time() < deadline:
        try:
            r = requests.get(url, timeout=3)
            if r.status_code < 500:
                return
        except Exception as exc:
            last_exc = exc
        time.sleep(3)
    raise TimeoutError(f"HTTP health at {url} not OK after {timeout}s — {last_exc}")


def _wait_grpc(host: str, port: int, timeout: int = 120) -> None:
    deadline = time.time() + timeout
    while time.time() < deadline:
        try:
            ch = grpc.insecure_channel(f"{host}:{port}")
            grpc.channel_ready_future(ch).result(timeout=3)
            ch.close()
            return
        except Exception:
            time.sleep(3)
    raise TimeoutError(f"gRPC at {host}:{port} not ready after {timeout}s")


# ──────────────────────────────────────────────────────────────────────────────
# Session fixtures — infrastructure
# ──────────────────────────────────────────────────────────────────────────────
@pytest.fixture(scope="session", autouse=True)
def built_images() -> None:
    """Build all three service images via docker compose (handles ../proto context)."""
    subprocess.check_call(
        ["docker", "compose", "-p", COMPOSE_PROJECT, "build"],
        cwd=str(REPO_ROOT / "infra"),
    )


@pytest.fixture(scope="session", autouse=True)
def generated_stubs() -> None:
    """Generate Python gRPC stubs and insert generated/ into sys.path."""
    subprocess.check_call(
        ["bash", str(IT_DIR / "generate_stubs.sh")],
        cwd=str(IT_DIR),
    )
    sys.path.insert(0, str(GENERATED_DIR))


@pytest.fixture(scope="session")
def docker_client():
    return docker.from_env()


@pytest.fixture(scope="session")
def docker_network(docker_client):
    try:
        docker_client.networks.get(NETWORK_NAME).remove()
    except docker.errors.NotFound:
        pass
    net = docker_client.networks.create(NETWORK_NAME, driver="bridge")
    yield net
    net.remove()


# ──────────────────────────────────────────────────────────────────────────────
# Session fixtures — service containers
# ──────────────────────────────────────────────────────────────────────────────
@pytest.fixture(scope="session")
def notification_svc(built_images, docker_network):
    c = DockerContainer(f"{COMPOSE_PROJECT}-notification-service")
    c.with_exposed_ports(50051, 8080)
    c.with_env("JWT_SECRET", JWT_SECRET)
    c.with_env("OTEL_EXPORTER_OTLP_ENDPOINT", _OTEL_NOOP)
    c.with_kwargs(
        network=NETWORK_NAME,
        hostname="notification-service",
    )
    c.start()
    grpc_port = int(c.get_exposed_port(50051))
    http_port = int(c.get_exposed_port(8080))
    _wait_http(f"http://localhost:{http_port}/actuator/health")
    yield {"grpc_port": grpc_port, "http_port": http_port, "container": c}
    c.stop()


@pytest.fixture(scope="session")
def subscription_svc(built_images, docker_network, notification_svc):
    c = DockerContainer(f"{COMPOSE_PROJECT}-subscription-service")
    c.with_exposed_ports(50052, 8081)
    c.with_env("JWT_SECRET", JWT_SECRET)
    c.with_env("GRPC_PORT", "50052")
    c.with_env("HTTP_PORT", "8081")
    c.with_env("NOTIFICATION_SERVICE_HOST", "notification-service")
    c.with_env("NOTIFICATION_SERVICE_PORT", "50051")
    c.with_env("OTEL_EXPORTER_OTLP_ENDPOINT", _OTEL_NOOP)
    c.with_kwargs(
        network=NETWORK_NAME,
        hostname="subscription-service",
    )
    c.start()
    grpc_port = int(c.get_exposed_port(50052))
    http_port = int(c.get_exposed_port(8081))
    _wait_http(f"http://localhost:{http_port}/health")
    yield {"grpc_port": grpc_port, "http_port": http_port, "container": c}
    c.stop()


@pytest.fixture(scope="session")
def analytics_svc(built_images, docker_network):
    c = DockerContainer(f"{COMPOSE_PROJECT}-analytics-service")
    c.with_exposed_ports(50053, 9091)
    c.with_env("JWT_SECRET", JWT_SECRET)
    c.with_env("GRPC_PORT", "50053")
    c.with_env("PROMETHEUS_PORT", "9091")
    c.with_env("OTEL_EXPORTER_OTLP_ENDPOINT", _OTEL_NOOP)
    c.with_kwargs(
        network=NETWORK_NAME,
        hostname="analytics-service",
    )
    c.start()
    grpc_port = int(c.get_exposed_port(50053))
    _wait_grpc("localhost", grpc_port)
    yield {"grpc_port": grpc_port, "container": c}
    c.stop()


# ──────────────────────────────────────────────────────────────────────────────
# Session fixtures — gRPC stubs + JWT
# ──────────────────────────────────────────────────────────────────────────────
@pytest.fixture(scope="session")
def pb2(generated_stubs):
    """Lazy-import generated modules after stubs are written to disk."""
    return {
        "n_pb2": importlib.import_module("notification.v1.notification_pb2"),
        "n_grpc": importlib.import_module("notification.v1.notification_pb2_grpc"),
        "s_pb2": importlib.import_module("subscription.v1.subscription_pb2"),
        "s_grpc": importlib.import_module("subscription.v1.subscription_pb2_grpc"),
        "a_pb2": importlib.import_module("analytics.v1.analytics_pb2"),
        "a_grpc": importlib.import_module("analytics.v1.analytics_pb2_grpc"),
    }


@pytest.fixture(scope="session")
def jwt_meta() -> list[tuple[str, str]]:
    payload = {
        "sub": "integration-test-user",
        "exp": datetime.datetime.utcnow() + datetime.timedelta(hours=2),
    }
    token = jwt.encode(payload, JWT_SECRET, algorithm="HS256")
    return [("authorization", f"Bearer {token}")]


@pytest.fixture(scope="session")
def stubs(notification_svc, subscription_svc, analytics_svc, pb2):
    ns_ch = grpc.insecure_channel(f"localhost:{notification_svc['grpc_port']}")
    ss_ch = grpc.insecure_channel(f"localhost:{subscription_svc['grpc_port']}")
    as_ch = grpc.insecure_channel(f"localhost:{analytics_svc['grpc_port']}")
    result = Stubs(
        ns=pb2["n_grpc"].NotificationServiceStub(ns_ch),
        ss=pb2["s_grpc"].SubscriptionServiceStub(ss_ch),
        as_=pb2["a_grpc"].AnalyticsServiceStub(as_ch),
        ns_ch=ns_ch,
        ss_ch=ss_ch,
        as_ch=as_ch,
    )
    yield result
    ns_ch.close()
    ss_ch.close()
    as_ch.close()
