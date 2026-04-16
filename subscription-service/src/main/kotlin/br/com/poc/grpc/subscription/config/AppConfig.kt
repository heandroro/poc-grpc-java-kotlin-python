package br.com.poc.grpc.subscription.config

data class AppConfig(
    val grpcPort: Int,
    val httpPort: Int,
    val notificationServiceHost: String,
    val notificationServicePort: Int,
    val jwtSecret: String,
    val otelEndpoint: String,
) {
    companion object {
        fun load() = AppConfig(
            grpcPort = System.getenv("GRPC_PORT")?.toInt() ?: 50052,
            httpPort = System.getenv("HTTP_PORT")?.toInt() ?: 8081,
            notificationServiceHost = System.getenv("NOTIFICATION_SERVICE_HOST") ?: "localhost",
            notificationServicePort = System.getenv("NOTIFICATION_SERVICE_PORT")?.toInt() ?: 50051,
            jwtSecret = System.getenv("JWT_SECRET") ?: "poc-grpc-super-secret-key-change-in-production",
            otelEndpoint = System.getenv("OTEL_EXPORTER_OTLP_ENDPOINT") ?: "http://jaeger:4317",
        )
    }
}
