"""
Best Practice: Centralized OpenTelemetry SDK configuration.
Configures a TracerProvider with OTLP gRPC exporter pointing to Jaeger.
"""
from __future__ import annotations

from opentelemetry import trace
from opentelemetry.exporter.otlp.proto.grpc.trace_exporter import OTLPSpanExporter
from opentelemetry.sdk.resources import Resource, SERVICE_NAME
from opentelemetry.sdk.trace import TracerProvider
from opentelemetry.sdk.trace.export import BatchSpanProcessor


def configure_otel(service_name: str, otlp_endpoint: str) -> None:
    resource = Resource.create({SERVICE_NAME: service_name})

    exporter = OTLPSpanExporter(endpoint=otlp_endpoint, insecure=True)
    provider = TracerProvider(resource=resource)
    provider.add_span_processor(BatchSpanProcessor(exporter))

    trace.set_tracer_provider(provider)
