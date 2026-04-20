# Tuning Guide — Referência de Configurações

## JVM Flags — Produção (container-aware)

```bash
java \
  -XX:+UseZGC -XX:+ZGenerational \
  -Xms512m -Xmx512m \
  -XX:MaxMetaspaceSize=256m \
  -XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=75.0 \
  -XX:+ExitOnOutOfMemoryError \
  -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/heapdump.hprof \
  -XX:+FlightRecorder \
  -XX:StartFlightRecording=duration=60s,filename=/tmp/recording.jfr \
  -jar app.jar
```

| Flag | Motivo |
|------|--------|
| `UseZGC` + `ZGenerational` | GC de baixíssima latência para containers |
| `-Xms` = `-Xmx` | Heap fixo evita resizing em containers |
| `UseContainerSupport` | JVM lê limites de CPU/memória do cgroup |
| `MaxRAMPercentage=75.0` | Reserva 25% para off-heap (metaspace, threads, NIO) |
| `ExitOnOutOfMemoryError` | Container reinicia em vez de ficar em estado ruim |
| `FlightRecorder` | Profiling contínuo com overhead < 1% |

## Virtual Threads

```yaml
spring:
  threads:
    virtual:
      enabled: true
```

- Habilita Virtual Thread Executor no Tomcat automaticamente.
- Evitar `synchronized` blocks longos — usar `ReentrantLock`.
- Não criar thread pools para Virtual Threads — criar sob demanda.
- Monitorar pinned threads: `-Djdk.tracePinnedThreads=short`.

## Web Server — Recomendação por cenário

| Cenário | Servidor | Justificativa |
|---------|----------|---------------|
| MVC tradicional (padrão) | **Tomcat** | Melhor suporte a Virtual Threads, padrão Spring Boot |
| Alta concorrência (>10k conn) | Undertow | Menor overhead por conexão via XNIO |
| Jetty legado | Jetty | Compatibilidade, menor memory footprint |
| WebFlux reativo | Netty | Event-loop otimizado para non-blocking |
| GraalVM Native | Tomcat/Netty | Ambos suportam native image |

## Caching

### Caffeine (local)

```yaml
spring:
  cache:
    type: caffeine
    caffeine:
      spec: maximumSize=10000,expireAfterWrite=5m
```

### Redis (distribuído)

```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: 6379
      timeout: 2s
  cache:
    type: redis
    redis:
      time-to-live: 10m
```

## HikariCP — Connection Pool

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 300000
      connection-timeout: 20000
      max-lifetime: 1800000
      leak-detection-threshold: 60000
```

**Regra de pool sizing:** `pool_size = (core_count × 2) + effective_spindle_count`

## Actuator para Tuning

```yaml
management:
  metrics:
    distribution:
      percentiles-histogram:
        http.server.requests: true
    tags:
      application: ${spring.application.name}
  tracing:
    sampling:
      probability: 1.0
```
