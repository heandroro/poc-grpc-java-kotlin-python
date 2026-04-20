# Guia de Dockerfile

## Multi-stage Build

```dockerfile
# Stage 1: Build
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app
COPY . .
RUN ./mvnw package -DskipTests

# Stage 2: Extract layers
FROM eclipse-temurin:25-jdk AS extractor
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --destination extracted

# Stage 3: Runtime
FROM eclipse-temurin:25-jre
WORKDIR /app

# Security: non-root user
RUN addgroup --system app && adduser --system --ingroup app app
USER app

# Layered copy (melhor cache)
COPY --from=extractor /app/extracted/dependencies/ ./
COPY --from=extractor /app/extracted/spring-boot-loader/ ./
COPY --from=extractor /app/extracted/snapshot-dependencies/ ./
COPY --from=extractor /app/extracted/application/ ./

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseZGC", "-XX:MaxRAMPercentage=75.0", 
            "org.springframework.boot.loader.launch.JarLauncher"]
```

## Boas Práticas

| Prática | Benefício |
|---------|-----------|
| Multi-stage | Imagem final menor |
| Layer extraction | Melhor cache de layers |
| Non-root user | Segurança |
| ZGC | GC eficiente para containers |
| MaxRAMPercentage | Respeita limites do container |

## Health Probes

```dockerfile
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health/liveness || exit 1
```

## Alternativas ao Dockerfile

| Ferramenta | Quando Usar |
|------------|-------------|
| **Jib** | Build rápido, sem Docker daemon |
| **Buildpacks** | Padrão Cloud Native, zero config |
| **GraalVM Native** | Startup < 100ms, menor memória |

## Tamanho de Imagem

- JRE base: ~80MB
- Com app layers: ~120MB
- Distroless: ~90MB
- GraalVM native: ~50MB
