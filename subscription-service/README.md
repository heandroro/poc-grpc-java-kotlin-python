# subscription-service

Serviço de gerenciamento de inscrições de usuários em tópicos. Implementado em **Kotlin + gRPC-Kotlin coroutines**, com chamadas downstream ao `notification-service` com propagação de deadline via `withTimeout`.

---

## Quick Start

```bash
# 1. Compilar e rodar testes
./gradlew build

# 2. Iniciar localmente
./gradlew run

# 3. Verificar saúde
curl http://localhost:8081/health
```

---

## Pré-requisitos

| Ferramenta | Versão mínima |
|---|---|
| Java (JDK) | 21 |
| Gradle | 8.8 (ou usar o wrapper `./gradlew`) |

> O Gradle Wrapper já está incluído — não é necessário instalar o Gradle manualmente.

---

## Estrutura de pacotes

```
src/main/kotlin/br/com/poc/grpc/subscription/
├── config/
│   └── AppConfig.kt          # Leitura de variáveis de ambiente
├── grpc/
│   ├── SubscriptionGrpcServer.kt   # Configuração do servidor gRPC
│   └── SubscriptionServiceImpl.kt  # Lógica de negócio (coroutines)
├── http/
│   └── HealthServer.kt       # Endpoint HTTP de health (Ktor)
├── interceptor/
│   ├── JwtClientInterceptor.kt     # Injeta JWT nas chamadas ao notification-service
│   └── LoggingClientInterceptor.kt # Logging estruturado de chamadas de saída
└── Main.kt                   # Entrypoint
```

---

## Variáveis de ambiente

| Variável | Padrão | Descrição |
|---|---|---|
| `JWT_SECRET` | `poc-grpc-super-secret-key-change-in-production` | Chave HMAC-SHA256 para geração de JWT outbound |
| `GRPC_PORT` | `50052` | Porta gRPC do serviço |
| `HTTP_PORT` | `8081` | Porta HTTP (health endpoint via Ktor) |
| `NOTIFICATION_SERVICE_HOST` | `localhost` | Host do `notification-service` |
| `NOTIFICATION_SERVICE_PORT` | `50051` | Porta gRPC do `notification-service` |
| `OTEL_EXPORTER_OTLP_ENDPOINT` | `http://jaeger:4317` | Endpoint OTLP para envio de traces |

---

## Build

```bash
# Compilar (inclui geração de stubs proto via protobuf plugin)
./gradlew compileKotlin

# Gerar distribuição executável
./gradlew installDist

# Build completo (compila + testa + verifica)
./gradlew build
```

O binário de distribuição fica em `build/install/subscription-service/bin/subscription-service`.

---

## Testes

### Executar testes unitários + cobertura + lint

```bash
./gradlew build
```

Ou separadamente:

```bash
# Testes + JaCoCo (Kover)
./gradlew koverVerify

# Somente lint ktlint
./gradlew ktlintCheck

# Corrigir automaticamente problemas de formatação
./gradlew ktlintFormat
```

Inclui:
- **JUnit 5 + MockK + AssertJ** — testes unitários em `src/test/kotlin/`
- **Kover** — cobertura de linha ≥ 90% (exclui `Main`, `AppConfig`, `SubscriptionGrpcServer`, `HealthServer`, e classes geradas)
- **ktlint 1.2.1** — estilo Kotlin padrão (exclui `generated/` e `build/`)

### Relatório de cobertura HTML

```bash
./gradlew koverHtmlReport
open build/reports/kover/html/index.html
```

### Classes cobertas pelos testes

| Classe | Responsabilidade testada |
|---|---|
| `SubscriptionServiceImplTest` | Subscribe (idempotência, validação), Unsubscribe, ListSubscriptions, PublishToSubscribers (sucesso, falha downstream, sem inscritos) |
| `JwtClientInterceptorTest` | Token Bearer injetado no header, formato JWT válido |
| `LoggingClientInterceptorTest` | Delegação da chamada, log de close OK e erro |

---

## Execução local

```bash
# Com configuração padrão
./gradlew run

# Apontando para notification-service em outro host
NOTIFICATION_SERVICE_HOST=192.168.1.100 ./gradlew run

# Com JWT e Jaeger customizados
JWT_SECRET=minha-chave \
OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317 \
./gradlew run
```

> **Atenção:** o `subscription-service` chama o `notification-service` em `PublishToSubscribers`. Certifique-se de que o `notification-service` esteja rodando antes de chamar esse RPC.

O serviço sobe em:
- `localhost:50052` — gRPC
- `localhost:8081` — HTTP health

---

## Docker

```bash
# Build da imagem (executar na raiz do repositório)
docker build -f subscription-service/Dockerfile -t subscription-service .

# Rodar apontando para notification-service local
docker run -p 50052:50052 -p 8081:8081 \
  -e NOTIFICATION_SERVICE_HOST=host.docker.internal \
  -e JWT_SECRET=minha-chave \
  subscription-service
```

> No ambiente completo use `cd infra && docker compose up --build` — veja [`infra/README.md`](../infra/README.md).

---

## API gRPC

O contrato completo está em [`proto/subscription/v1/subscription.proto`](../proto/subscription/v1/subscription.proto).

### Gerar token de teste

```bash
TOKEN=$(python3 -c "
import jwt, datetime
secret='poc-grpc-super-secret-key-change-in-production'
payload={'sub':'user-001','exp': datetime.datetime.utcnow()+datetime.timedelta(hours=1)}
print(jwt.encode(payload, secret, algorithm='HS256'))
")
```

### Subscribe (unary)

```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{
    "user_id": "user-001",
    "topic": "promos",
    "min_priority": "NOTIFICATION_PRIORITY_NORMAL"
  }' \
  localhost:50052 subscription.v1.SubscriptionService/Subscribe
```

### Unsubscribe (unary)

```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{"user_id": "user-001", "topic": "promos"}' \
  localhost:50052 subscription.v1.SubscriptionService/Unsubscribe
```

### ListSubscriptions (server streaming)

```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{"user_id": "user-001", "active_only": true}' \
  localhost:50052 subscription.v1.SubscriptionService/ListSubscriptions
```

### PublishToSubscribers (unary → fan-out)

```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{
    "topic": "promos",
    "title": "Flash Sale",
    "body": "Apenas hoje!",
    "priority": "NOTIFICATION_PRIORITY_CRITICAL"
  }' \
  localhost:50052 subscription.v1.SubscriptionService/PublishToSubscribers
```
