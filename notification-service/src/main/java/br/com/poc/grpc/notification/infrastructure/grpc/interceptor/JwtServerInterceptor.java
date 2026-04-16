package br.com.poc.grpc.notification.infrastructure.grpc.interceptor;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * Best Practice: Use a ServerInterceptor to validate JWTs before any handler runs.
 * Extracts the caller identity and propagates it via gRPC Context.
 */
@GrpcGlobalServerInterceptor
public class JwtServerInterceptor implements ServerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JwtServerInterceptor.class);

    public static final Metadata.Key<String> AUTHORIZATION_KEY =
        Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);

    public static final Context.Key<String> USER_ID_CTX_KEY = Context.key("userId");

    private final SecretKey signingKey;

    public JwtServerInterceptor(@Value("${notification.jwt.secret}") String secret) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        ServerCallHandler<ReqT, RespT> next
    ) {
        String authHeader = headers.get(AUTHORIZATION_KEY);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or malformed Authorization header for method={}", call.getMethodDescriptor().getFullMethodName());
            call.close(Status.UNAUTHENTICATED.withDescription("Missing Bearer token"), new Metadata());
            return new ServerCall.Listener<>() {};
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

            String userId = claims.getSubject();
            log.debug("Authenticated request userId={} method={}", userId, call.getMethodDescriptor().getFullMethodName());

            Context ctx = Context.current().withValue(USER_ID_CTX_KEY, userId);
            return Contexts.interceptCall(ctx, call, headers, next);

        } catch (JwtException e) {
            log.warn("Invalid JWT token error={}", e.getMessage());
            call.close(Status.UNAUTHENTICATED.withDescription("Invalid token: " + e.getMessage()), new Metadata());
            return new ServerCall.Listener<>() {};
        }
    }
}
