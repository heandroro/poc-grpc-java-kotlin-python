FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY ../proto /proto
COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn package -DskipTests -q

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 50051 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
