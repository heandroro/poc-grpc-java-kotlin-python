"""
Best Practice: gRPC interceptors for JWT validation and structured logging in Python.
"""
from __future__ import annotations

import time
import jwt
import grpc
import structlog

log = structlog.get_logger(__name__)

_AUTHORIZATION_KEY = "authorization"
_JWT_SECRET = None  # set via configure()


def configure(jwt_secret: str) -> None:
    global _JWT_SECRET
    _JWT_SECRET = jwt_secret


class JwtServerInterceptor(grpc.ServerInterceptor):
    """
    Best Practice: Validates JWT on every incoming RPC.
    Service-to-service calls inject a token; missing/invalid token → UNAUTHENTICATED.
    """

    def intercept_service(self, continuation, handler_call_details):
        metadata = dict(handler_call_details.invocation_metadata)
        auth_header = metadata.get(_AUTHORIZATION_KEY, "")

        if not auth_header.startswith("Bearer "):
            return _abort_handler(grpc.StatusCode.UNAUTHENTICATED, "Missing Bearer token")

        token = auth_header[len("Bearer "):]
        try:
            payload = jwt.decode(token, _JWT_SECRET, algorithms=["HS256"])
            log.info("jwt_validated", subject=payload.get("sub"), method=handler_call_details.method)
        except jwt.PyJWTError as exc:
            log.warning("jwt_invalid", error=str(exc))
            return _abort_handler(grpc.StatusCode.UNAUTHENTICATED, f"Invalid token: {exc}")

        return continuation(handler_call_details)


class LoggingServerInterceptor(grpc.ServerInterceptor):
    """
    Best Practice: Structured logging for every gRPC call — logs method, status and latency.
    """

    def intercept_service(self, continuation, handler_call_details):
        handler = continuation(handler_call_details)
        method = handler_call_details.method

        if handler is None:
            return handler

        def wrap_unary(behaviour):
            def wrapper(request, context):
                start = time.monotonic()
                try:
                    result = behaviour(request, context)
                    elapsed_ms = int((time.monotonic() - start) * 1000)
                    log.info("grpc_call_completed", method=method, latency_ms=elapsed_ms)
                    return result
                except Exception as exc:
                    elapsed_ms = int((time.monotonic() - start) * 1000)
                    log.warning("grpc_call_failed", method=method, error=str(exc), latency_ms=elapsed_ms)
                    raise
            return wrapper

        def wrap_stream(behaviour):
            def wrapper(request_or_iter, context):
                start = time.monotonic()
                try:
                    yield from behaviour(request_or_iter, context)
                    elapsed_ms = int((time.monotonic() - start) * 1000)
                    log.info("grpc_stream_completed", method=method, latency_ms=elapsed_ms)
                except Exception as exc:
                    elapsed_ms = int((time.monotonic() - start) * 1000)
                    log.warning("grpc_stream_failed", method=method, error=str(exc), latency_ms=elapsed_ms)
                    raise
            return wrapper

        if handler.request_streaming or handler.response_streaming:
            return handler._replace(unary_stream=wrap_stream(handler.unary_stream) if not handler.request_streaming else None,
                                    stream_stream=wrap_stream(handler.stream_stream) if handler.stream_stream else None,
                                    unary_unary=None)

        return handler._replace(unary_unary=wrap_unary(handler.unary_unary))


def _abort_handler(code: grpc.StatusCode, details: str):
    def handler(request, context):
        context.abort(code, details)
    return grpc.unary_unary_rpc_method_handler(handler)
