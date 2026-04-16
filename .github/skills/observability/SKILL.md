---
name: observability
description: Observabilidade com Spring Boot Actuator, Micrometer, Prometheus, OpenTelemetry, distributed tracing e health probes
---

# Observability

Skill para observabilidade completa em projetos Java 25 + Spring Boot 4 — métricas, logs, traces, e health checks.

## Recursos Bundled

### `examples/` — Código de Exemplo
| Arquivo | Conteúdo |
|---------|----------|
| `ObservabilityExamples.java` | HealthIndicator, ReactiveHealthIndicator, métricas customizadas, @Observed |
| `observability-config.yml` | application.yml completo com Actuator, Micrometer, tracing |
| `alerting-rules.yml` | Prometheus scrape config + Alertmanager rules |
| `logback-spring.xml` | Logging JSON (prod) e texto (local) com trace ID |

### `scripts/` — Configurações de Build
| Arquivo | Conteúdo |
|---------|----------|
| `maven-observability.xml` | Dependências Maven: Actuator, Micrometer, OpenTelemetry, Logback JSON |

## Pilares da Observabilidade

- **Métricas** — Micrometer (Counter, Timer, Gauge) exportadas para Prometheus
- **Logs** — JSON estruturado com trace ID via MDC (Logback)
- **Traces** — OpenTelemetry via Micrometer Tracing bridge
- **Health** — Probes liveness/readiness/startup para Kubernetes

## Dependências

Ver `scripts/maven-observability.xml` para lista completa de dependências.

## Configuração Actuator

- Endpoints expostos: `health`, `info`, `metrics`, `prometheus`, `loggers`, `threaddump`, `heapdump`.
- Health probes: `management.endpoint.health.probes.enabled=true`.
- Grupos: `liveness` (livenessState), `readiness` (readinessState, db, redis, kafka).
- Tags obrigatórias: `application`, `environment`, `region`.

Ver `examples/observability-config.yml` para configuração completa.

## Health Probes

- `HealthIndicator` — verificação síncrona (banco, cache).
- `ReactiveHealthIndicator` — verificação assíncrona (APIs externas via WebClient).
- Timeout máximo: 1 segundo por indicator.

Ver `examples/ObservabilityExamples.java` para implementação.

## Métricas Customizadas

- `Counter.builder("orders.created").tag("type", "online").register(registry)` — contadores.
- `Timer.builder("orders.processing.time").publishPercentiles(0.5, 0.95, 0.99)` — latência.
- `Gauge.builder("orders.queue.size", queue, Queue::size)` — valores observados.

Ver `examples/ObservabilityExamples.java` para implementação.

## Logging Estruturado

- JSON em produção (`!local` profile); texto legível em desenvolvimento (`local`).
- Trace ID automaticamente incluído via MDC (`%X{traceId}`).
- `StructuredArguments.kv("orderId", id)` para campos adicionais.

Ver `examples/logback-spring.xml` para configuração.

## Distributed Tracing

- `@Observed(name = "order.service")` — instrumenta métodos automaticamente.
- `ObservedAspect` bean habilita a anotação.
- `ObservationFilter` adiciona dados de contexto (user.id, tenant).
- Propagação via header `traceparent` (W3C).

Ver `examples/ObservabilityExamples.java` para implementação.

## Endpoints Principais

| Endpoint | Descrição | Uso |
|----------|-----------|-----|
| `/actuator/health` | Health geral | Load balancers, K8s |
| `/actuator/health/liveness` | Liveness probe | Kubernetes |
| `/actuator/health/readiness` | Readiness probe | Kubernetes |
| `/actuator/health/startup` | Startup probe | Kubernetes (slow start) |
| `/actuator/metrics` | Lista de métricas | Debug |
| `/actuator/metrics/{name}` | Métrica específica | Debug |
| `/actuator/prometheus` | Métricas no formato Prometheus | Scraping |
| `/actuator/info` | Info build/git | Dashboards |
| `/actuator/loggers` | Níveis de log dinâmicos | Operações |
| `/actuator/threaddump` | Thread dump | Debugging |
| `/actuator/heapdump` | Heap dump | Memory leaks |

## Dashboards e Alerting

- **Prometheus**: scrape `/actuator/prometheus` a cada 15s.
- **Alertas recomendados**: HighErrorRate (>1%), SlowResponseTimeP99 (>500ms), HighMemoryUsage (>85%), ApplicationDown.

Ver `examples/alerting-rules.yml` para configuração completa de Prometheus + Alertmanager.

## Convenções

- Sempre usar `Observation` para tracing (não `@Span` diretamente)
- Métricas devem ter tags consistentes (`application`, `environment`, `region`)
- Health indicators devem ser rápidos (< 1s timeout)
- Logs estruturados (JSON) em produção; texto em desenvolvimento
- Não expor endpoints sensíveis (`env`, `configprops`) sem autenticação
- Usar `management.server.port` diferente para isolamento (opcional)
- Propagar trace ID em todas as chamadas HTTP via `traceparent` header (W3C)
