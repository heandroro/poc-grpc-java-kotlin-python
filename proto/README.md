# proto

Contratos Protobuf compartilhados entre os três serviços. Versionados explicitamente (`.v1`) e gerenciados com **buf v2** para lint, detecção de breaking changes e geração de código multi-linguagem.

---

## Estrutura

```
proto/
├── buf.yaml                           # Lint + breaking change detection
├── buf.gen.yaml                       # Geração de código (Java, Kotlin, Python)
├── notification/v1/notification.proto # NotificationService
├── subscription/v1/subscription.proto # SubscriptionService
└── analytics/v1/analytics.proto      # AnalyticsService
```

---

## Serviços e RPCs

### `notification.v1.NotificationService`

| RPC | Tipo | Descrição |
|---|---|---|
| `SendNotification` | Unary | Envia uma notificação para um usuário |
| `StreamNotifications` | Server streaming | Stream de notificações em tempo real filtradas por tópico/prioridade |
| `NotificationChannel` | Bidirecional streaming | Canal de ACK e atualização de estado |

### `subscription.v1.SubscriptionService`

| RPC | Tipo | Descrição |
|---|---|---|
| `Subscribe` | Unary | Inscreve usuário em um tópico com prioridade mínima |
| `Unsubscribe` | Unary | Remove inscrição de usuário |
| `ListSubscriptions` | Server streaming | Lista inscrições ativas de um usuário |
| `PublishToSubscribers` | Unary | Fan-out: envia notificação para todos os inscritos de um tópico |

### `analytics.v1.AnalyticsService`

| RPC | Tipo | Descrição |
|---|---|---|
| `RecordEvent` | Unary | Registra um evento de ciclo de vida de notificação |
| `GetStats` | Unary | Retorna estatísticas agregadas por tópico ou usuário |
| `StreamMetrics` | Server streaming | Snapshots periódicos de métricas por tópico |

---

## Pré-requisitos

```bash
# macOS
brew install bufbuild/buf/buf

# Linux / Windows: https://buf.build/docs/installation
```

Verificar instalação:

```bash
buf --version
# buf 1.x.x
```

---

## Lint

Valida naming conventions, comentários obrigatórios, package versioning e demais regras do ruleset `BASIC + DEFAULT`.

```bash
cd proto
buf lint
# Saída vazia = sem erros
```

Regras ativas (`buf.yaml`):
- `BASIC` + `DEFAULT` — inclui naming, field ordering, enum zero-value, etc.
- Exceção: `PACKAGE_VERSION_SUFFIX` desabilitado (packages já usam `.v1` manualmente)

---

## Breaking Change Detection

Detecta mudanças incompatíveis com clientes existentes (campo removido, tipo alterado, numeração reutilizada, etc.).

```bash
cd proto

# Comparar com a branch main
buf breaking --against '.git#branch=main'

# Comparar com a última tag
buf breaking --against '.git#tag=v1.0.0'

# Comparar com o HEAD anterior
buf breaking --against '.git#ref=HEAD~1'
```

Política (`buf.yaml`): `FILE` — verifica compatibilidade em nível de arquivo (mais restrito que `PACKAGE`).

---

## Geração de código

O `buf.gen.yaml` gera stubs para Java/Kotlin e Python usando plugins remotos do BSR (Buf Schema Registry).

```bash
cd proto
buf generate
```

Saída gerada:

| Linguagem | Destino | Plugin |
|---|---|---|
| Java (proto) | `notification-service/src/main/java` | `buf.build/protocolbuffers/java` |
| Java (gRPC) | `notification-service/src/main/java` | `buf.build/grpc/java` |
| Java (proto) | `subscription-service/src/main/java` | `buf.build/protocolbuffers/java` |
| Java (gRPC) | `subscription-service/src/main/java` | `buf.build/grpc/java` |
| Python (proto) | `analytics-service/src/generated` | `buf.build/protocolbuffers/python` |
| Python (gRPC) | `analytics-service/src/generated` | `buf.build/grpc/python` |

> Para Java/Kotlin, os stubs também são gerados automaticamente durante o build Maven/Gradle via `protobuf-maven-plugin`. Para Python, use `scripts/generate_proto.sh` ou `buf generate`.

---

## Convenções de nomenclatura

| Elemento | Convenção | Exemplo |
|---|---|---|
| Package | `snake_case.vN` | `notification.v1` |
| Service | `PascalCase` | `NotificationService` |
| RPC | `PascalCase` | `SendNotification` |
| Message | `PascalCase` | `SendNotificationRequest` |
| Field | `snake_case` | `user_id`, `notification_id` |
| Enum type | `PascalCase` | `NotificationPriority` |
| Enum value | `SCREAMING_SNAKE_CASE` com prefixo | `NOTIFICATION_PRIORITY_HIGH` |

### Padrões adotados

- **Requests/Responses** — cada RPC tem seu próprio par de mensagens (`XxxRequest` / `XxxResponse`)
- **Zero-value seguro** — todos os enums têm valor `_UNSPECIFIED = 0`
- **Timestamps** — `google.protobuf.Timestamp` para datas
- **Erros ricos** — `google.rpc.Status` + `google.rpc.ErrorInfo` para detalhes de erro estruturados
- **`oneof`** — usado em `GetStatsRequest` para filtros mutuamente exclusivos

---

## Adicionar um novo serviço

1. Criar `proto/<nome>/v1/<nome>.proto` com `package <nome>.v1;`
2. Rodar `buf lint` para validar
3. Adicionar plugin de geração em `buf.gen.yaml` se necessário
4. Rodar `buf generate`
5. Rodar `buf breaking --against '.git#branch=main'` para garantir compatibilidade
