FROM gradle:8.8-jdk21 AS build
WORKDIR /workspace

COPY ../proto /proto
COPY build.gradle.kts settings.gradle.kts ./
RUN gradle dependencies --no-daemon -q

COPY src ./src
RUN gradle installDist --no-daemon -q

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /workspace/build/install/subscription-service ./
EXPOSE 50052 8081
ENTRYPOINT ["./bin/subscription-service"]
