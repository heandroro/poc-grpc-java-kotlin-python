package br.com.poc.grpc.subscription

import br.com.poc.grpc.subscription.config.AppConfig
import br.com.poc.grpc.subscription.grpc.SubscriptionGrpcServer
import br.com.poc.grpc.subscription.http.startHealthServer
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("br.com.poc.grpc.subscription.Main")

fun main(): Unit = runBlocking {
    val config = AppConfig.load()
    log.info("Starting subscription-service port={}", config.grpcPort)

    val grpcServer = SubscriptionGrpcServer(config)
    grpcServer.start()

    startHealthServer(config.httpPort)

    log.info("subscription-service ready grpcPort={} httpPort={}", config.grpcPort, config.httpPort)
    grpcServer.awaitTermination()
}
