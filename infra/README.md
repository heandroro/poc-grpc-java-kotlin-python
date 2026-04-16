# infra

Infraestrutura local do PoC usando **Docker Compose**. Orquestra os três serviços gRPC e as ferramentas de observabilidade (Jaeger e Prometheus) em uma rede Docker isolada.

---

## Quick Start

```bash
cd infra

# Subir tudo (build + start)
docker compose up --build

# Em background
docker compose up --build -d
```

Aguardar os healthchecks ficarem verdes (~30–60s) e então acessar:

| URL | Descrição |
|---|---|
| `localhost:50051` | notification-service gRPC |
| `localhost:50052` | subscription-service gRPC |
| `localhost:50053` | analytics-service gRPC |
| http://localhost:8080/actuator/health | notification-service health |
| http://localhost:8081/health | subscription-service health |
| http://localhost:9091/metrics | analytics-service métricas Prometheus |
| http://localhost:16686 | **Jaeger UI** — traces distribuídos |
| http://localhost:9090 | **Prometheus** — métricas |

---

## Pré-requisitos

| Ferramenta | Versão mínima |
|---|---|
| Docker | 24 |
| Docker Compose | v2 (plugin — `docker compose`, não `docker-compose`) |

---

## Variáveis de ambiente

Todas as variáveis compartilhadas são injetadas via âncora YAML `x-common-env` no `docker-compose.yml`.

| Variável | Padrão | Onde é usada |
|---|---|---|
| `JWT_SECRET` | `poc-grpc-super-secret-key-change-in-production` | Todos os serviços (validação/geração de JWT) |
| `OTEL_EXPORTER_OTLP_ENDPOINT` | `http://jaeger:4317` | Todos os serviços (envio de traces) |

Para substituir o `JWT_SECRET`:

```bash
JWT_SECRET=minha-chave-segura docker compose up --build
```

Ou via arquivo `.env` na pasta `infra/`:

```bash
echo "JWT_SECRET=minha-chave-segura" > infra/.env
cd infra && docker compose up --build
```

---

## Serviços e topologia

```
                    ┌─────────────────────────┐
                    │        grpc-net          │  (bridge network)
                    │                         │
  ┌─────────────────┤                         ├─────────────────┐
  │                 │   notification-service  │                 │
  │  :50051/:8080   │   (Java / Spring Boot)  │                 │
  │                 └────────────┬────────────┘                 │
  │                              │ healthcheck                  │
  │                 ┌────────────▼────────────┐                 │
  │                 │  subscription-service   │                 │
  │  :50052/:8081   │    (Kotlin / gRPC-Kt)   │                 │
  │                 │  depends_on: notif ✓    │                 │
  │                 └─────────────────────────┘                 │
  │                                                             │
  │                 ┌─────────────────────────┐                 │
  │                 │   analytics-service     │                 │
  │  :50053/:9091   │     (Python / grpcio)   │                 │
  │                 └─────────────────────────┘                 │
  │                                                             │
  │  :4317/:16686   ┌─────────────────────────┐                 │
  └─────────────────│  jaeger (all-in-one)    │─────────────────┘
                    │  OTLP gRPC + UI         │
                    └─────────────────────────┘
                    ┌─────────────────────────┐
  :9090             │       prometheus        │
                    │  depends_on: notif+anlyt│
                    └─────────────────────────┘
```

### Ordem de start (depends_on)

1. **jaeger** — inicia sem dependências
2. **notification-service** — depende de `jaeger` (started)
3. **subscription-service** — depende de `notification-service` (healthy ✓)
4. **analytics-service** — depende de `jaeger` (started)
5. **prometheus** — depende de `notification-service` + `analytics-service`

---

## Comandos úteis

```bash
# Ver logs de todos os serviços
docker compose logs -f

# Logs de um serviço específico
docker compose logs -f notification-service

# Parar sem remover volumes
docker compose stop

# Parar e remover containers + rede
docker compose down

# Rebuild forçado de um serviço específico
docker compose up --build notification-service

# Ver status dos healthchecks
docker compose ps
```

---

## Observabilidade

### Jaeger — Traces distribuídos

Acesse **http://localhost:16686** após executar um fluxo completo.

- Selecione um serviço (`notification-service`, `subscription-service`, `analytics-service`) no dropdown
- Clique em **Find Traces** para ver os spans
- Traces cruzam serviços via propagação W3C TraceContext (OTel)

Portas expostas pelo Jaeger:

| Porta | Protocolo | Uso |
|---|---|---|
| `16686` | HTTP | UI |
| `4317` | gRPC | OTLP ingest (serviços → Jaeger) |
| `4318` | HTTP | OTLP ingest alternativo |

### Prometheus — Métricas

Acesse **http://localhost:9090** para consultar métricas via PromQL.

Scrape configurado em `prometheus.yml`:

| Job | Target | Endpoint |
|---|---|---|
| `notification-service` | `notification-service:8080` | `/actuator/prometheus` |
| `subscription-service` | `subscription-service:8081` | `/metrics` |
| `analytics-service` | `analytics-service:9091` | `/metrics` |

Exemplos de queries PromQL:

```promql
# Contagem de RPCs por método (notification-service)
grpc_server_calls_total

# Latência P99 das chamadas
histogram_quantile(0.99, rate(grpc_server_handling_seconds_bucket[5m]))

# Total de eventos registrados (analytics-service)
analytics_events_total
```

---

## Configuração do Prometheus

O arquivo `prometheus.yml` define os targets de scrape. Para adicionar um novo target:

```yaml
scrape_configs:
  - job_name: meu-servico
    static_configs:
      - targets: ["meu-servico:porta"]
    metrics_path: /metrics
```

Reiniciar o Prometheus após alterar:

```bash
docker compose restart prometheus
```
