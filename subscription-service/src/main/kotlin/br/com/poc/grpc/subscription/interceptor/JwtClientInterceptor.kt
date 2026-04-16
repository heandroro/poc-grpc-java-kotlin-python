package br.com.poc.grpc.subscription.interceptor

import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.ForwardingClientCall
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets
import java.util.Date

/**
 * Best Practice: ClientInterceptor that injects a JWT Bearer token into every outgoing RPC.
 * Demonstrates token propagation between services (subscription → notification).
 */
class JwtClientInterceptor(secret: String) : ClientInterceptor {

    private val log = LoggerFactory.getLogger(JwtClientInterceptor::class.java)

    companion object {
        val AUTHORIZATION_KEY: Metadata.Key<String> =
            Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER)
    }

    private val signingKey = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

    override fun <ReqT, RespT> interceptCall(
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        next: Channel,
    ): ClientCall<ReqT, RespT> {
        return object : ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
            next.newCall(method, callOptions)
        ) {
            override fun start(responseListener: Listener<RespT>, headers: Metadata) {
                val token = buildToken()
                headers.put(AUTHORIZATION_KEY, "Bearer $token")
                log.debug("Injecting JWT for outbound call method={}", method.fullMethodName)
                super.start(responseListener, headers)
            }
        }
    }

    private fun buildToken(): String {
        val now = Date()
        val exp = Date(now.time + 3_600_000)
        return Jwts.builder()
            .subject("subscription-service")
            .issuedAt(now)
            .expiration(exp)
            .signWith(signingKey)
            .compact()
    }
}
