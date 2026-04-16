"""Unit tests for JwtServerInterceptor and LoggingServerInterceptor."""
from unittest.mock import MagicMock, call
import grpc
import jwt
import pytest

import interceptors


_SECRET = "test-jwt-secret-minimum-32-bytes-!!!"


@pytest.fixture(autouse=True)
def configure_jwt():
    interceptors.configure(_SECRET)
    yield
    interceptors.configure(None)


def _make_handler_call_details(auth_header=None, method="/TestService/TestMethod"):
    details = MagicMock()
    details.method = method
    metadata = {}
    if auth_header is not None:
        metadata["authorization"] = auth_header
    details.invocation_metadata = metadata
    return details


def _valid_token(subject="svc-client"):
    return jwt.encode({"sub": subject, "exp": 9999999999}, _SECRET, algorithm="HS256")


class TestJwtServerInterceptor:
    @pytest.fixture()
    def interceptor(self):
        return interceptors.JwtServerInterceptor()

    def test_should_abort_when_authorization_header_is_missing(self, interceptor):
        details = _make_handler_call_details()
        context = MagicMock()
        handler = interceptor.intercept_service(lambda _: None, details)

        handler.unary_unary(None, context)
        context.abort.assert_called_once_with(grpc.StatusCode.UNAUTHENTICATED, "Missing Bearer token")

    def test_should_abort_when_no_bearer_prefix(self, interceptor):
        details = _make_handler_call_details(auth_header="Basic sometoken")
        context = MagicMock()
        handler = interceptor.intercept_service(lambda _: None, details)

        handler.unary_unary(None, context)
        context.abort.assert_called_once_with(grpc.StatusCode.UNAUTHENTICATED, "Missing Bearer token")

    def test_should_abort_when_token_is_invalid(self, interceptor):
        details = _make_handler_call_details(auth_header="Bearer this.is.invalid.jwt")
        context = MagicMock()
        handler = interceptor.intercept_service(lambda _: None, details)

        handler.unary_unary(None, context)
        context.abort.assert_called_once()
        code = context.abort.call_args[0][0]
        assert code == grpc.StatusCode.UNAUTHENTICATED

    def test_should_call_continuation_when_token_is_valid(self, interceptor):
        token = _valid_token()
        details = _make_handler_call_details(auth_header=f"Bearer {token}")
        continuation = MagicMock(return_value=MagicMock(spec=grpc.unary_unary_rpc_method_handler(lambda r, c: None)))

        interceptor.intercept_service(continuation, details)

        continuation.assert_called_once_with(details)

    def test_should_abort_when_jwt_secret_is_not_configured(self, interceptor):
        interceptors.configure(None)
        token = _valid_token()
        details = _make_handler_call_details(auth_header=f"Bearer {token}")
        context = MagicMock()
        handler = interceptor.intercept_service(lambda _: None, details)

        handler.unary_unary(None, context)
        context.abort.assert_called_once()


class TestLoggingServerInterceptor:
    @pytest.fixture()
    def interceptor(self):
        return interceptors.LoggingServerInterceptor()

    def test_should_wrap_unary_handler_and_call_original_behaviour(self, interceptor):
        expected_result = object()
        behaviour = MagicMock(return_value=expected_result)
        original_handler = grpc.unary_unary_rpc_method_handler(behaviour)
        continuation = MagicMock(return_value=original_handler)
        details = _make_handler_call_details()

        wrapped_handler = interceptor.intercept_service(continuation, details)
        request = MagicMock()
        context = MagicMock()
        result = wrapped_handler.unary_unary(request, context)

        behaviour.assert_called_once_with(request, context)
        assert result is expected_result

    def test_should_propagate_exception_from_unary_handler(self, interceptor):
        def failing_behaviour(request, context):
            raise RuntimeError("handler error")

        original_handler = grpc.unary_unary_rpc_method_handler(failing_behaviour)
        continuation = MagicMock(return_value=original_handler)
        details = _make_handler_call_details()

        wrapped_handler = interceptor.intercept_service(continuation, details)
        with pytest.raises(RuntimeError, match="handler error"):
            wrapped_handler.unary_unary(MagicMock(), MagicMock())

    def test_should_return_none_handler_unchanged(self, interceptor):
        continuation = MagicMock(return_value=None)
        details = _make_handler_call_details()

        result = interceptor.intercept_service(continuation, details)

        assert result is None
