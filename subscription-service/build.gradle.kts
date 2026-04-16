import com.google.protobuf.gradle.id

plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
    id("com.google.protobuf") version "0.9.4"
    id("io.ktor.plugin") version "2.3.12"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    id("org.jetbrains.kotlinx.kover") version "0.7.6"
    application
}

group = "br.com.poc.grpc"
version = "0.0.1-SNAPSHOT"

val grpcVersion = "1.63.0"
val grpcKotlinVersion = "1.4.1"
val protobufVersion = "3.25.3"
val ktorVersion = "2.3.12"
val otelVersion = "1.37.0"

repositories {
    mavenCentral()
}

dependencies {
    // gRPC Kotlin
    implementation("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("io.grpc:grpc-netty-shaded:$grpcVersion")
    implementation("io.grpc:grpc-services:$grpcVersion")
    implementation("com.google.protobuf:protobuf-kotlin:$protobufVersion")
    implementation("com.google.api.grpc:proto-google-common-protos:2.41.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")

    // Ktor (HTTP for health/metrics)
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")

    // OpenTelemetry
    implementation("io.opentelemetry:opentelemetry-api:$otelVersion")
    implementation("io.opentelemetry:opentelemetry-sdk:$otelVersion")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp:$otelVersion")
    implementation("io.opentelemetry:opentelemetry-sdk-extension-autoconfigure:$otelVersion")

    // Micrometer Prometheus
    implementation("io.micrometer:micrometer-registry-prometheus:1.13.0")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")

    // Test
    testImplementation(kotlin("test"))
    testImplementation("io.grpc:grpc-testing:$grpcVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testImplementation("io.mockk:mockk:1.13.11")
    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
}

application {
    mainClass.set("br.com.poc.grpc.subscription.MainKt")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:$grpcKotlinVersion:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
    sourceSets {
        main {
            proto {
                srcDir("../proto")
            }
        }
    }
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

ktlint {
    version.set("1.2.1")
    android.set(false)
    outputToConsole.set(true)
    filter {
        exclude { element -> element.file.path.contains("generated") }
        exclude { element -> element.file.path.contains("build") }
    }
}

koverReport {
    filters {
        excludes {
            classes(
                "br.com.poc.grpc.subscription.MainKt",
                "br.com.poc.grpc.subscription.Main*",
                "br.com.poc.grpc.subscription.config.*",
                "br.com.poc.grpc.subscription.grpc.SubscriptionGrpcServer*",
                "br.com.poc.grpc.subscription.http.*",
                "br.com.poc.grpc.notification.v1.*",
                "br.com.poc.grpc.subscription.v1.*",
                "br.com.poc.grpc.analytics.v1.*",
            )
        }
    }
    verify {
        rule {
            bound {
                minValue = 90
            }
        }
    }
}
