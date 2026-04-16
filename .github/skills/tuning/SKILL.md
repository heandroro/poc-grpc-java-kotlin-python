---
name: tuning
description: Performance tuning com JVM flags, GraalVM Native, Virtual Threads, Structured Concurrency, caching e connection pools
---

# Tuning

Skill para performance tuning de aplicações Java 25 + Spring Boot 4.

## Recursos Bundled

### `references/` — Documentação Técnica
| Arquivo | Conteúdo |
|---------|----------|
| `tuning-guide.md` | JVM flags, Virtual Threads, web servers, Caffeine, Redis, HikariCP, JFR |

## JVM Flags

### Produção (container-aware)

- `-XX:+UseZGC -XX:+ZGenerational` — GC de baixa latência.
- `-Xms` = `-Xmx` — heap fixo em containers evita resizing.
- `-XX:MaxRAMPercentage=75.0` — reserva 25% para off-heap.
- `-XX:+UseContainerSupport` — lê limites de CPU/memória do cgroup.
- `-XX:+ExitOnOutOfMemoryError` — container reinicia em vez de ficar degradado.
- `-XX:+FlightRecorder` — profiling contínuo com overhead < 1%.

### GraalVM Native Image

- `./mvnw -Pnative native:compile` ou `./gradlew nativeCompile`.
- `--enable-preview` para features Java 25.
- `-H:+ReportExceptionStackTraces` para debug.

## Virtual Threads

- Habilitar com `spring.threads.virtual.enabled=true`.
- Evitar `synchronized` blocks longos — usar `ReentrantLock`.
- Não criar thread pools para Virtual Threads; criar sob demanda.
- Monitorar threads presas: `-Djdk.tracePinnedThreads=short`.

## Embedded Web Server

### Recomendação por cenário

| Cenário | Web Server | Justificativa |
|---------|------------|---------------|
| **MVC tradicional** (padrão) | **Tomcat** | Melhor Virtual Threads support, padrão Spring Boot |
| **Alta concorrência** (>10k conn) | Undertow | Menor overhead por conexão |
| **Jetty legado** | Jetty | Compatibilidade, menor memory footprint |
| **WebFlux reativo** | Netty | Event-loop otimizado para non-blocking |
| **GraalVM Native** | Tomcat/Netty | Ambos suportam native image |

## Structured Concurrency

- `StructuredTaskScope.ShutdownOnFailure` — cancela todas as subtarefas se uma falhar.
- `scope.fork(() -> ...)` — executa cada subtarefa em Virtual Thread independente.
- `scope.join().throwIfFailed()` — aguarda conclusão e propaga exceções.

## Caching

- **Caffeine** (local): `spring.cache.type=caffeine` com `spec=maximumSize=10000,expireAfterWrite=5m`.
- **Redis** (distribuído): `spring.cache.type=redis` com `time-to-live=10m`.

## Connection Pool (HikariCP)

- Regra de sizing: `pool_size = (core_count × 2) + effective_spindle_count`.
- `leak-detection-threshold=60000` — detecta conexões vazadas em 60s.
- `max-lifetime=1800000` — recicla conexões a cada 30min (antes do timeout do banco).

## Observabilidade

- Habilitar `management.metrics.distribution.percentiles-histogram.http.server.requests=true`.
- Tags obrigatórias: `application`, `environment`, `region`.
- Ver skill `observability` para configuração completa.

## Profiling e diagnóstico

- **Java Flight Recorder (JFR)** — profiling contínuo em produção com overhead mínimo
- **async-profiler** — CPU e allocation profiling para hotspots
- **jcmd** — diagnóstico em runtime (heap dump, thread dump, JFR)
- **Micrometer** — métricas customizadas com tags para dashboards Grafana

## Convenções

- Definir JVM flags via variáveis de ambiente (`JAVA_OPTS`)
- Heap size fixo em containers (`-Xms` = `-Xmx`)
- Monitorar GC pauses, allocation rate, e thread count
- Benchmark antes e depois de otimizações (JMH)
- Tuning de pool sizes baseado em load testing, não em guesses
- Revisar configurações de tuning a cada major release
