package br.com.poc.grpc.notification.infrastructure.grpc.interceptor;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoggingServerInterceptorTest {

    @Mock(answer = org.mockito.Answers.RETURNS_DEEP_STUBS)
    private ServerCall<Object, Object> call;

    @Mock
    private ServerCallHandler<Object, Object> next;

    @Mock
    private ServerCall.Listener<Object> listener;

    private LoggingServerInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new LoggingServerInterceptor();
    }

    @Test
    void should_delegate_to_next_and_return_listener() {
        var headers = new Metadata();
        when(next.startCall(any(), any())).thenReturn(listener);

        var result = interceptor.interceptCall(call, headers, next);

        verify(next).startCall(any(), eq(headers));
        assertThat(result).isEqualTo(listener);
    }

    @Test
    void should_log_and_delegate_close_on_ok_status() {
        var headers = new Metadata();
        ArgumentCaptor<ServerCall<Object, Object>> callCaptor = ArgumentCaptor.forClass(ServerCall.class);
        when(next.startCall(callCaptor.capture(), any())).thenReturn(listener);

        interceptor.interceptCall(call, headers, next);

        callCaptor.getValue().close(Status.OK, new Metadata());

        verify(call).close(eq(Status.OK), any());
    }

    @Test
    void should_log_warning_and_delegate_close_on_error_status() {
        var headers = new Metadata();
        ArgumentCaptor<ServerCall<Object, Object>> callCaptor = ArgumentCaptor.forClass(ServerCall.class);
        when(next.startCall(callCaptor.capture(), any())).thenReturn(listener);

        interceptor.interceptCall(call, headers, next);

        var errorStatus = Status.INTERNAL.withDescription("something failed");
        callCaptor.getValue().close(errorStatus, new Metadata());

        verify(call).close(eq(errorStatus), any());
    }
}
