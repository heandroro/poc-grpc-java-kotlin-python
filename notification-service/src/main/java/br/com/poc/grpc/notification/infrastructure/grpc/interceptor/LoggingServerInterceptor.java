package br.com.poc.grpc.notification.infrastructure.grpc.interceptor;

import io.grpc.ForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Best Practice: Structured logging with MDC context for every gRPC call.
 * Logs method name, userId (from context), status code and latency.
 */
@GrpcGlobalServerInterceptor
public class LoggingServerInterceptor implements ServerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingServerInterceptor.class);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        ServerCallHandler<ReqT, RespT> next
    ) {
        String method = call.getMethodDescriptor().getFullMethodName();
        long startNanos = System.nanoTime();

        MDC.put("grpc.method", method);

        var wrappedCall = new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
            @Override
            public void close(Status status, Metadata trailers) {
                long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000;
                MDC.put("grpc.status", status.getCode().name());
                MDC.put("grpc.latency_ms", String.valueOf(elapsedMs));
                if (status.isOk()) {
                    log.info("gRPC call completed method={} status={} latency_ms={}", method, status.getCode(), elapsedMs);
                } else {
                    log.warn("gRPC call failed method={} status={} description={} latency_ms={}",
                        method, status.getCode(), status.getDescription(), elapsedMs);
                }
                MDC.remove("grpc.status");
                MDC.remove("grpc.latency_ms");
                super.close(status, trailers);
            }
        };

        try {
            return next.startCall(wrappedCall, headers);
        } finally {
            MDC.remove("grpc.method");
        }
    }
}
