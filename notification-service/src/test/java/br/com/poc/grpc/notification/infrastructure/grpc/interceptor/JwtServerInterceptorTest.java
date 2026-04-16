package br.com.poc.grpc.notification.infrastructure.grpc.interceptor;

import io.grpc.Context;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.Status;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServerInterceptorTest {

    private static final String TEST_SECRET = "test-secret-key-for-unit-tests-minimum-32bytes!!";

    @Mock(answer = org.mockito.Answers.RETURNS_DEEP_STUBS)
    private ServerCall<Object, Object> call;

    @Mock
    private ServerCallHandler<Object, Object> next;

    private JwtServerInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new JwtServerInterceptor(TEST_SECRET);
    }

    @Test
    void should_reject_when_authorization_header_is_missing() {
        var headers = new Metadata();

        interceptor.interceptCall(call, headers, next);

        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(call).close(statusCaptor.capture(), any());
        assertThat(statusCaptor.getValue().getCode()).isEqualTo(Status.UNAUTHENTICATED.getCode());
        verifyNoInteractions(next);
    }

    @Test
    void should_reject_when_bearer_prefix_is_missing() {
        var headers = new Metadata();
        headers.put(JwtServerInterceptor.AUTHORIZATION_KEY, "Basic sometoken");

        interceptor.interceptCall(call, headers, next);

        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(call).close(statusCaptor.capture(), any());
        assertThat(statusCaptor.getValue().getCode()).isEqualTo(Status.UNAUTHENTICATED.getCode());
        verifyNoInteractions(next);
    }

    @Test
    void should_reject_when_token_is_invalid() {
        var headers = new Metadata();
        headers.put(JwtServerInterceptor.AUTHORIZATION_KEY, "Bearer this.is.not.a.valid.jwt");

        interceptor.interceptCall(call, headers, next);

        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(call).close(statusCaptor.capture(), any());
        assertThat(statusCaptor.getValue().getCode()).isEqualTo(Status.UNAUTHENTICATED.getCode());
        verifyNoInteractions(next);
    }

    @Test
    void should_propagate_user_id_in_context_when_token_is_valid() {
        String token = buildToken("user-007");
        var headers = new Metadata();
        headers.put(JwtServerInterceptor.AUTHORIZATION_KEY, "Bearer " + token);

        var capturedContext = new AtomicReference<Context>();
        when(next.startCall(any(), any())).thenAnswer(inv -> {
            capturedContext.set(Context.current());
            return mock(ServerCall.Listener.class);
        });

        interceptor.interceptCall(call, headers, next);

        verify(next).startCall(any(), any());
        assertThat(JwtServerInterceptor.USER_ID_CTX_KEY.get(capturedContext.get())).isEqualTo("user-007");
    }

    private String buildToken(String subject) {
        var key = Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
            .subject(subject)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 3_600_000))
            .signWith(key)
            .compact();
    }
}
