# analytics-service

Serviço de métricas e analytics de notificações. Implementado em **Python 3.12 + grpcio**, com ingestão de eventos de ciclo de vida (`RecordEvent`), estatísticas agregadas (`GetStats`) e streaming de snapshots em tempo real (`StreamMetrics`).

---

## Quick Start

```bash
# 1. Instalar dependências
pip install -r requirements.txt

# 2. Gerar stubs Protobuf
bash scripts/generate_proto.sh

# 3. Rodar testes
pytest

# 4. Iniciar localmente
python src/main.py
```

---

## Pré-requisitos

| Ferramenta | Versão mínima |
|---|---|
| Python | 3.12 |
| pip | 23+ |

> Os stubs gRPC Python são gerados via `grpc_tools.protoc` — é necessário executar `generate_proto.sh` **antes** de iniciar o serviço localmente. No Docker, isso é feito automaticamente.

---

## Estrutura

```
analytics-service/
├── src/
│   ├── main.py               # Entrypoint: gRPC server + Prometheus HTTP server
│   ├── analytics_service.py  # AnalyticsServiceImpl + EventStore (in-memory)
│   ├── interceptors.py       # JwtServerInterceptor, LoggingServerInterceptor
│   ├── otel_setup.py         # Configuração do OpenTelemetry SDK
│   └── generated/            # Stubs gerados (não versionar) — criados por generate_proto.sh
├── tests/
│   ├── conftest.py           # Mock dos módulos proto para testes sem geração
│   ├── test_event_store.py
│   ├── test_analytics_service.py
│   └── test_interceptors.py
├── scripts/
│   └── generate_proto.sh     # Geração local de stubs Python
├── requirements.txt
└── pyproject.toml            # Configuração de pytest, pytest-cov e ruff
```

---

## Variáveis de ambiente

| Variável | Padrão | Descrição |
|---|---|---|
| `JWT_SECRET` | `poc-grpc-super-secret-key-change-in-production` | Chave para validação de JWT inbound |
| `GRPC_PORT` | `50053` | Porta gRPC do serviço |
| `PROMETHEUS_PORT` | `9091` | Porta HTTP para exposição de métricas Prometheus |
| `OTEL_EXPORTER_OTLP_ENDPOINT` | `http://jaeger:4317` | Endpoint OTLP para envio de traces |

---

## Instalação

```bash
# Criar e ativar virtualenv (recomendado)
python3 -m venv .venv
source .venv/bin/activate

# Instalar todas as dependências (produção + teste + lint)
pip install -r requirements.txt
```

---

## Geração de stubs Protobuf

Necessário apenas para execução local — no Docker é feito automaticamente.

```bash
# Executar a partir do diretório analytics-service/
bash scripts/generate_proto.sh
```

Os stubs são gerados em `src/generated/`. Esse diretório **não deve ser versionado**.

---

## Testes

Os testes rodam **sem necessidade de gerar stubs** — o `conftest.py` cria mocks dos módulos proto automaticamente.

### Executar todos os testes com cobertura

```bash
pytest
```

Configuração em `pyproject.toml` inclui `--cov=src --cov-fail-under=90` por padrão.

### Opções úteis

```bash
# Relatório detalhado por linha
pytest --cov=src --cov-report=term-missing

# Relatório HTML
pytest --cov=src --cov-report=html
open htmlcov/index.html

# Rodar somente uma classe de testes
pytest tests/test_event_store.py -v

# Parar no primeiro erro
pytest -x
```

### Lint com ruff

```bash
# Verificar
ruff check src/ tests/

# Corrigir automaticamente
ruff check --fix src/ tests/
```

### Classes cobertas pelos testes

| Módulo | Responsabilidade testada |
|---|---|
| `test_event_store.py` | Acumulação de contagens por tópico/usuário, snapshot, thread-safety de cópia |
| `test_analytics_service.py` | `GetStats` (filtro por tópico, usuário, sem filtro), `RecordEvent` (campos obrigatórios), `StreamMetrics` (cancelamento por `is_active`) |
| `test_interceptors.py` | JWT (ausente, sem Bearer, inválido, válido, secret não configurado), Logging (wrapping unary, propagação de exceção, handler `None`) |

---

## Execução local

```bash
# Com configuração padrão
python src/main.py

# Com variáveis customizadas
GRPC_PORT=50053 \
PROMETHEUS_PORT=9091 \
JWT_SECRET=minha-chave \
OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317 \
python src/main.py
```

O serviço sobe em:
- `localhost:50053` — gRPC
- `localhost:9091` — HTTP métricas Prometheus (`/metrics`)

---

## Docker

```bash
# Build da imagem (executar na raiz do repositório)
# Nota: a geração de stubs proto acontece durante o build da imagem
docker build -f analytics-service/Dockerfile -t analytics-service .

# Rodar isolado
docker run -p 50053:50053 -p 9091:9091 \
  -e JWT_SECRET=minha-chave \
  analytics-service
```

> No ambiente completo use `cd infra && docker compose up --build` — veja [`infra/README.md`](../infra/README.md).

---

## API gRPC

O contrato completo está em [`proto/analytics/v1/analytics.proto`](../proto/analytics/v1/analytics.proto).

### Gerar token de teste

```bash
TOKEN=$(python3 -c "
import jwt, datetime
secret='poc-grpc-super-secret-key-change-in-production'
payload={'sub':'svc-client','exp': datetime.datetime.utcnow()+datetime.timedelta(hours=1)}
print(jwt.encode(payload, secret, algorithm='HS256'))
")
```

### RecordEvent (unary)

```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{
    "notification_id": "n-001",
    "user_id": "user-001",
    "topic": "promos",
    "state": "NOTIFICATION_STATE_DELIVERED"
  }' \
  localhost:50053 analytics.v1.AnalyticsService/RecordEvent
```

### GetStats por tópico (unary)

```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{"topic": "promos"}' \
  localhost:50053 analytics.v1.AnalyticsService/GetStats
```

### GetStats por usuário (unary)

```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{"user_id": "user-001"}' \
  localhost:50053 analytics.v1.AnalyticsService/GetStats
```

### StreamMetrics (server streaming)

```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{"interval_seconds": 5, "topics": ["promos"]}' \
  localhost:50053 analytics.v1.AnalyticsService/StreamMetrics
```
