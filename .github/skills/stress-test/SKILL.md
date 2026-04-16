---
name: stress-test
description: Testes de carga e performance com Gatling 4, JMH, k6, Micrometer e Prometheus
---

# Stress Test

Skill para testes de carga e performance em projetos Java 25 + Spring Boot 4.

## Recursos Bundled

### `examples/` — Código de Exemplo
| Arquivo | Conteúdo |
|---------|----------|
| `OrderSimulation.java` | Gatling 4 — cenários de carga com ramp-up, assertions de SLA, multi-scenario |
| `SerializationBenchmark.java` | JMH — benchmarks de serialização e cálculo com `@Param`, `Blackhole` |

## Ferramentas obrigatórias

- **Gatling 4** — testes de carga HTTP com DSL Scala/Java e relatórios HTML
- **JMH (Java Microbenchmark Harness)** — microbenchmarks para hotspots de código
- **k6** — testes de carga scriptáveis em JavaScript para cenários complexos
- **Micrometer + Prometheus** — métricas de aplicação durante testes

## Métricas de SLA

| Métrica | Threshold |
|---|---|
| p50 latência | ≤ 100ms |
| p95 latência | ≤ 300ms |
| p99 latência | ≤ 500ms |
| Throughput | ≥ 1000 req/s (ajustar por endpoint) |
| Taxa de erro | ≤ 0.1% |
| CPU sob carga | ≤ 70% |
| Memória heap | ≤ 80% do max |

## Cenários de teste

- **Smoke test** — carga mínima para validar que o teste funciona (5 users, 1 min)
- **Load test** — carga esperada em produção (100 users, 10 min)
- **Stress test** — acima da capacidade para encontrar o ponto de ruptura (ramp up até falha)
- **Soak test** — carga constante por período longo para detectar memory leaks (1h+)
- **Spike test** — picos súbitos de carga (0 → 500 users em 10s)

## Estrutura Gatling

- Extends `Simulation`; defina `HttpProtocolBuilder`, `ScenarioBuilder` e `setUp()`.
- Injetar carga com `rampUsers(n).during(d)` ou `constantUsersPerSec(n).during(d)`.
- Assertions obrigatórias: `global().responseTime().percentile(99).lt(500)` e `successfulRequests().percent().gt(99.9)`.
- Salvar variáveis entre requests com `.check(jsonPath("$.id").saveAs("orderId"))`.

## Estrutura JMH

- Anotar classe com `@State(Scope.Benchmark)`, `@Warmup`, `@Measurement`, `@Fork`.
- Métodos de benchmark anotados com `@Benchmark`.
- Usar `Blackhole` para consumir resultados e evitar dead-code elimination.
- `@Param` para testar múltiplas entradas no mesmo benchmark.

Ver `examples/OrderSimulation.java` e `examples/SerializationBenchmark.java` para implementação completa.

## Convenções

- Testes de stress rodam em ambiente dedicado (não em CI padrão)
- Resultados devem ser comparados com baseline registrado
- Executar testes de carga antes de cada release para produção
- Gatling reports devem ser arquivados como artefatos do CI
- JMH deve ser usado para validar otimizações pontuais (serialização, parsing, algoritmos)
- Monitorar GC pauses durante testes de carga via JFR (Java Flight Recorder)
