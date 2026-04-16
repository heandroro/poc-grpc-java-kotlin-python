package br.com.poc.grpc.subscription.grpc

import br.com.poc.grpc.subscription.config.AppConfig
import br.com.poc.grpc.subscription.interceptor.JwtClientInterceptor
import br.com.poc.grpc.subscription.interceptor.LoggingClientInterceptor
import br.com.poc.grpc.notification.v1.NotificationServiceGrpcKt
import io.grpc.ManagedChannelBuilder
import io.grpc.ServerBuilder
import org.slf4j.LoggerFactory

class SubscriptionGrpcServer(private val config: AppConfig) {

    private val log = LoggerFactory.getLogger(SubscriptionGrpcServer::class.java)

    private val notificationChannel = ManagedChannelBuilder
        .forAddress(config.notificationServiceHost, config.notificationServicePort)
        .usePlaintext()
        .intercept(JwtClientInterceptor(config.jwtSecret), LoggingClientInterceptor())
        .build()

    private val notificationStub = NotificationServiceGrpcKt.NotificationServiceCoroutineStub(notificationChannel)

    private val server = ServerBuilder
        .forPort(config.grpcPort)
        .addService(SubscriptionServiceImpl(notificationStub))
        .build()

    fun start() {
        server.start()
        log.info("gRPC server started port={}", config.grpcPort)
        Runtime.getRuntime().addShutdownHook(Thread {
            log.info("Shutting down gRPC server")
            server.shutdown()
            notificationChannel.shutdown()
        })
    }

    fun awaitTermination() = server.awaitTermination()
}
