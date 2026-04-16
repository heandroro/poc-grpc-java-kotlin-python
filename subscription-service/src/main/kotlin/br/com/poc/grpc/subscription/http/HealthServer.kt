package br.com.poc.grpc.subscription.http

import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun startHealthServer(port: Int) {
    embeddedServer(Netty, port = port) {
        routing {
            get("/health") {
                call.respond(mapOf("status" to "UP", "service" to "subscription-service"))
            }
            get("/metrics") {
                call.respond("# subscription-service metrics placeholder")
            }
        }
    }.start(wait = false)
}
