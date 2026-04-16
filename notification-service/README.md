# notification-service

Serviço de envio e streaming de notificações. Implementado em **Java 21 + Spring Boot 3** com arquitetura hexagonal, expondo três RPCs gRPC: unary, server-streaming e bidirecional.

---

## Quick Start

```bash
# 1. Compilar e rodar testes
mvn verify

# 2. Iniciar localmente
mvn spring-boot:run

# 3. Verificar saúde
curl http://localhost:8080/actuator/health
```

---

## Pré-requisitos

| Ferramenta | Versão mínima |
|---|---|
| Java (JDK) | 21 |
| Maven | 3.9 |

> Os stubs Protobuf são gerados automaticamente pelo `protobuf-maven-plugin` durante o build — nenhum passo manual necessário.

---

## Estrutura de pacotes

```
src/main/java/br/com/poc/grpc/notification/
├── domain/
│   ├── model/          # Notification, NotificationPriority, NotificationState (records/enums)
│   └── port/           # NotificationRepository, NotificationPublisher (interfaces)
├── application/
│   ├── dto/            # SendNotificationCommand
│   └── usecase/        # SendNotificationUseCase
├── adapter/
│   └── grpc/           # NotificationGrpcAdapter, NotificationProtoMapper
└── infrastructure/
    ├── grpc/interceptor/   # JwtServerInterceptor, LoggingServerInterceptor
    ├── persistence/        # InMemoryNotificationRepository
    └── pubsub/             # InMemoryNotificationPublisher
```

---

## Variáveis de ambiente

| Variável | Padrão | Descrição |
|---|---|---|
| `JWT_SECRET` | `poc-grpc-super-secret-key-change-in-production` | Chave HMAC-SHA256 para validação de JWT |
| `OTEL_EXPORTER_OTLP_ENDPOINT` | `http://jaeger:4317` | Endpoint OTLP para envio de traces |
| `GRPC_SERVER_PORT` | `50051` | Porta gRPC (configurada em `application.yml`) |
| `SERVER_PORT` | `8080` | Porta HTTP (Actuator / Prometheus) |

---

## Build

```bash
# Compilar e gerar stubs proto
mvn compile

# Gerar JAR executável (sem testes)
mvn package -DskipTests

# Build completo: compilação + testes + relatórios
mvn verify
```

O JAR gerado fica em `target/notification-service-*.jar`.

---

## Testes

### Executar testes unitários + cobertura + lint

```bash
mvn verify
```

Inclui:
- **JUnit 5** — testes unitários em `src/test/java/`
- **JaCoCo** — cobertura ≥ 90% de instrução, ≥ 85% de branch (falha o build se não atingir)
- **Checkstyle** — lint baseado em Google Java Style com indentação 4-espaços

### Somente testes (sem lint)

```bash
mvn test
```

### Somente lint

```bash
mvn checkstyle:check
```

### Relatório de cobertura HTML

```bash
mvn verify
open target/site/jacoco/index.html
```

### Classes cobertas pelos testes

| Classe | Responsabilidade testada |
|---|---|
| `SendNotificationUseCaseTest` | Salva no repositório e publica no publisher |
| `NotificationProtoMapperTest` | Mapeamento bidirecional de todos os enums |
| `JwtServerInterceptorTest` | Rejeição sem token, token inválido, propagação de `userId` |
| `LoggingServerInterceptorTest` | Delegação ao próximo handler, log OK e erro |
| `InMemoryNotificationRepositoryTest` | CRUD, filtros por tópico e prioridade |
| `InMemoryNotificationPublisherTest` | Entrega, múltiplos subscribers, unsubscribe, falha isolada |
| `NotificationGrpcAdapterTest` | Todos os três RPCs (unary, streaming, bidirecional) |

---

## Execução local

```bash
# Com variáveis padrão (sem Jaeger)
mvn spring-boot:run

# Apontando para um Jaeger local
OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317 mvn spring-boot:run

# Com JWT customizado
JWT_SECRET=minha-chave-secreta mvn spring-boot:run
```

O serviço sobe em:
- `localhost:50051` — gRPC
- `localhost:8080` — HTTP (Actuator, Prometheus)

---

## Docker

```bash
# Build da imagem (executar na raiz do repositório)
docker build -f notification-service/Dockerfile -t notification-service .

# Rodar isolado
docker run -p 50051:50051 -p 8080:8080 \
  -e JWT_SECRET=minha-chave \
  notification-service
```

> No ambiente Docker Compose completo use `cd infra && docker compose up --build` — veja [`infra/README.md`](../infra/README.md).

---

## API gRPC

O contrato completo está em [`proto/notification/v1/notification.proto`](../proto/notification/v1/notification.proto).

### Gerar token de teste

```bash
TOKEN=$(python3 -c "
import jwt, datetime
secret='poc-grpc-super-secret-key-change-in-production'
payload={'sub':'user-001','exp': datetime.datetime.utcnow()+datetime.timedelta(hours=1)}
print(jwt.encode(payload, secret, algorithm='HS256'))
")
```

### SendNotification (unary)

```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{
    "user_id": "user-001",
    "topic": "promos",
    "title": "Oferta especial",
    "body": "50% de desconto hoje",
    "priority": "NOTIFICATION_PRIORITY_HIGH"
  }' \
  localhost:50051 notification.v1.NotificationService/SendNotification
```

### StreamNotifications (server streaming)

```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{
    "user_id": "user-001",
    "topics": ["promos"],
    "min_priority": "NOTIFICATION_PRIORITY_LOW"
  }' \
  localhost:50051 notification.v1.NotificationService/StreamNotifications
```

### NotificationChannel (bidirecional streaming)

```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{"notification_id": "n-001", "user_id": "user-001"}' \
  localhost:50051 notification.v1.NotificationService/NotificationChannel
```

---

## Endpoints HTTP

| Endpoint | Descrição |
|---|---|
| `GET /actuator/health` | Status do serviço |
| `GET /actuator/prometheus` | Métricas Prometheus |
| `GET /actuator/metrics` | Métricas Spring |
