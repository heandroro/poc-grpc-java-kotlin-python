# integration-tests

Testes de integração end-to-end que sobem os três serviços em containers Docker e exercitam os fluxos gRPC completos.

---

## Pré-requisitos

| Ferramenta | Versão mínima | Observação |
|---|---|---|
| Python | 3.10+ | necessário para rodar pytest |
| Docker | 24+ | Docker Engine ou Docker Desktop |
| Docker Compose | v2 (plugin) | `docker compose` (sem hífen) |

> Os containers são construídos automaticamente na primeira execução — **não é necessário** subir o `docker compose` manualmente antes dos testes.

---

## Instalação

```bash
cd integration-tests
pip install -r requirements.txt
```

---

## Estrutura

```
integration-tests/
├── conftest.py          # Fixtures de sessão: build, network, containers, stubs, JWT
├── generate_stubs.sh    # Gera stubs Python a partir de proto/
├── generated/           # Stubs gerados (gitignored; criados automaticamente)
├── requirements.txt     # Dependências Python
└── test_e2e.py          # 5 cenários E2E
```

---

## Executar

### Via Makefile (recomendado)

```bash
# a partir da raiz do repositório
make test-integration
```

O target `test-integration`:
1. Instala as dependências Python
2. Gera os stubs proto em `generated/`
3. Executa `pytest test_e2e.py -v --timeout=120`

### Diretamente

```bash
cd integration-tests
bash generate_stubs.sh
pytest test_e2e.py -v --timeout=120
```

---

## Ciclo de vida das fixtures (sessão)

```
docker compose -p poc-test build        ← imagens construídas com o mesmo contexto do docker-compose.yml
        ↓
generate_stubs.sh                       ← stubs Python gerados de proto/
        ↓
Rede Docker  poc-integration-net        ← bridge isolada para comunicação entre containers
        ↓
notification-service   :50051 :8080     ← aguarda GET /actuator/health 200
        ↓
subscription-service   :50052 :8081     ← NOTIFICATION_SERVICE_HOST=notification-service
                                           aguarda GET /health 200
        ↓
analytics-service      :50053 :9091     ← aguarda porta gRPC pronta
        ↓
pytest session                          ← channels gRPC abertos para os 3 serviços
```

Todas as fixtures têm escopo `session`: os containers são iniciados **uma única vez** para toda a suíte.

---

## Cenários de teste (`test_e2e.py`)

| Teste | Serviços | Fluxo |
|---|---|---|
| `test_send_notification_unary` | notification | `SendNotification` → assert `notification_id` e `accepted_at` |
| `test_subscribe_and_list_subscriptions` | subscription | `Subscribe` → `ListSubscriptions` (streaming) → assert inscrição ativa |
| `test_publish_fanout_reaches_subscribers` | subscription → notification | 2 inscritos → `PublishToSubscribers` → assert `notifications_sent == 2` |
| `test_stream_notifications_receives_live_notification` | notification | Stream aberto em thread → `SendNotification` → assert notificação recebida no stream |
| `test_analytics_record_event_and_get_stats` | analytics | 4 × `RecordEvent` → `GetStats` → assert contadores e `delivery_rate > 0` |

---

## Variáveis de ambiente usadas nos containers

| Variável | Valor nos testes | Serviço |
|---|---|---|
| `JWT_SECRET` | `poc-grpc-super-secret-key-change-in-production` | todos |
| `OTEL_EXPORTER_OTLP_ENDPOINT` | `http://localhost:14317` (no-op) | todos |
| `NOTIFICATION_SERVICE_HOST` | `notification-service` (hostname Docker) | subscription |
| `NOTIFICATION_SERVICE_PORT` | `50051` | subscription |
| `GRPC_PORT` | `50052` / `50053` | subscription, analytics |
| `HTTP_PORT` | `8081` | subscription |
| `PROMETHEUS_PORT` | `9091` | analytics |

> Jaeger e Prometheus **não são iniciados** nos testes de integração — o endpoint OTLP aponta para uma URL inexistente e os serviços continuam funcionando normalmente (OTel falha silenciosamente).

---

## Geração de stubs proto

O script `generate_stubs.sh` gera os stubs a partir de `proto/` usando `grpc_tools.protoc`:

```bash
bash generate_stubs.sh
```

Saída em `generated/`:
```
generated/
├── notification/v1/notification_pb2.py
├── notification/v1/notification_pb2_grpc.py
├── subscription/v1/subscription_pb2.py
├── subscription/v1/subscription_pb2_grpc.py
├── analytics/v1/analytics_pb2.py
└── analytics/v1/analytics_pb2_grpc.py
```

Os arquivos em `generated/` são **gitignored** e recriados a cada execução.
