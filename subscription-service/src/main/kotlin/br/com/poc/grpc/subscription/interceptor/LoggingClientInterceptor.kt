package br.com.poc.grpc.subscription.interceptor

import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.ForwardingClientCall
import io.grpc.ForwardingClientCallListener
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.grpc.Status
import org.slf4j.LoggerFactory

/**
 * Best Practice: Structured logging interceptor measuring latency and capturing status for every outgoing call.
 */
class LoggingClientInterceptor : ClientInterceptor {

    private val log = LoggerFactory.getLogger(LoggingClientInterceptor::class.java)

    override fun <ReqT, RespT> interceptCall(
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        next: Channel,
    ): ClientCall<ReqT, RespT> {
        val startNanos = System.nanoTime()
        val methodName = method.fullMethodName

        return object : ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
            next.newCall(method, callOptions)
        ) {
            override fun start(responseListener: Listener<RespT>, headers: Metadata) {
                super.start(
                    object : ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                        override fun onClose(status: Status, trailers: Metadata) {
                            val elapsedMs = (System.nanoTime() - startNanos) / 1_000_000
                            if (status.isOk) {
                                log.info("gRPC client call completed method={} status={} latency_ms={}", methodName, status.code, elapsedMs)
                            } else {
                                log.warn("gRPC client call failed method={} status={} description={} latency_ms={}", methodName, status.code, status.description, elapsedMs)
                            }
                            super.onClose(status, trailers)
                        }
                    },
                    headers
                )
            }
        }
    }
}
