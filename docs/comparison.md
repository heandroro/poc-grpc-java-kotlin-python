# gRPC vs REST vs GraphQL: Análise Comparativa

Este documento apresenta uma análise técnica dos três principais paradigmas de comunicação entre serviços, com foco em trade-offs relevantes para arquiteturas de microserviços.

---

## 1. Visão Geral

| Paradigma | Origem | Modelo de Dados | Transporte | Tipagem |
|---|---|---|---|---|
| **REST** | Roy Fielding (2000) | Recursos (JSON/XML) | HTTP 1.1/2 | Fraca (schema opcional) |
| **gRPC** | Google (2015) | Protobuf (contratos binários) | HTTP/2 | Forte (schema obrigatório) |
| **GraphQL** | Facebook (2015) | Grafo de entidades | HTTP (geralmente) | Forte (schema obrigatório) |

---

## 2. Performance

### 2.1 Latência

| Aspecto | REST | gRPC | GraphQL |
|---|---|---|---|
| Serialização | JSON textual (lento) | Protobuf binário (rápido) | JSON textual |
| Compressão | Gzip (opcional) | Inerente (binário) | Gzip (opcional) |
| Handshake | HTTP 1.1 = múltiplo | HTTP/2 multiplexado | HTTP 1.1 ou HTTP/2 |
| Latência típica | 10-50ms | 2-10ms | 10-50ms+ (N+1) |

> **gRPC vence**: Protobuf é 5-10x mais compacto e rápido que JSON. HTTP/2 elimina head-of-line blocking.

### 2.2 Throughput

```
Payload típico: 1KB de dados estruturados

REST (JSON):     ~800 bytes após compressão gzip
Protobuf:        ~150-200 bytes (sem compressão necessária)
GraphQL:         ~900+ bytes (overhead de queries)
```

**Implicação**: Em alta escala, REST requer 4-5x mais banda que gRPC.

### 2.3 Streaming

| Capacidade | REST | gRPC | GraphQL |
|---|---|---|---|
| Server Streaming | SSE (limitado) | ✅ Nativo | ✅ Subscriptions |
| Client Streaming | ❌ Não suportado | ✅ Nativo | ❌ Não suportado |
| Bidirecional | WebSocket (complicado) | ✅ Nativo | WebSocket |

> **gRPC vence**: Streaming é primeiro-cidadão; em REST requer protocolos adicionais.

---

## 3. Developer Experience

### 3.1 Curva de Aprendizado

| Aspecto | REST | gRPC | GraphQL |
|---|---|---|---|
| Facilidade inicial | ⭐⭐⭐⭐⭐ Simples | ⭐⭐⭐ Moderada | ⭐⭐⭐⭐ Fácil |
| Tooling maduro | ⭐⭐⭐⭐⭐ Swagger, Postman | ⭐⭐⭐⭐ grpcurl, Evans | ⭐⭐⭐⭐ Playground |
| Debugging | ⭐⭐⭐⭐⭐ curl, browser dev tools | ⭐⭐⭐ Requer ferramentas específicas | ⭐⭐⭐⭐ Playground inspecionável |

### 3.2 Code Generation

| Capacidade | REST | gRPC | GraphQL |
|---|---|---|---|
| Client SDK | Swagger/OpenAPI (geralmente manual) | ✅ Automático (toda stack) | ✅ Automático (com codegen) |
| Server stubs | Framework-dependente | ✅ Automático | Resolvers manuais |
| Type safety | Fraca/Doc apenas | ✅ Forte (runtime + compile-time) | ✅ Forte (compile-time) |

> **gRPC vence**: "Contratos como código" gera stubs para 10+ linguagens automaticamente.

### 3.3 Versionamento

| Estratégia | REST | gRPC | GraphQL |
|---|---|---|---|
| URL versioning | `/v1/users` (comum) | Não aplicável | Não aplicável |
| Header versioning | `Accept: application/vnd.v1+json` | ✅ Binário compatível via field numbers | Deprecations + `@deprecated` |
| Schema evolution | Manual/quebrável | ✅ Forward/backward compat nativo | ✅ Aditivo por design |

> **Protobuf vence**: Field numbers permitem evolução sem breaking changes.

---

## 4. Interoperabilidade

### 4.1 Suporte a Clientes

| Cliente | REST | gRPC | GraphQL |
|---|---|---|---|
| Browser/SPA | ⭐⭐⭐⭐⭐ Nativo | ⭐⭐ Requer gRPC-Web + proxy | ⭐⭐⭐⭐⭐ Excelente |
| Mobile (iOS/Android) | ⭐⭐⭐⭐⭐ Bem suportado | ⭐⭐⭐⭐ Libraries nativas disponíveis | ⭐⭐⭐⭐⭐ Excelente |
| CLI/curl | ⭐⭐⭐⭐⭐ curl direto | ⭐⭐ Requer grpcurl | ⭐⭐ Requer ferramentas |
| IoT/Embedded | ⭐⭐⭐ Pesado | ⭐⭐⭐⭐ gRPC lightweight funciona | ⭐⭐ Pesado |

### 4.2 Firewall/Proxy

| Aspecto | REST | gRPC | GraphQL |
|---|---|---|---|
| Compatibilidade HTTP padrão | ✅ Sim | ⚠️ Requer HTTP/2 support | ✅ Sim |
| Load balancing L7 | ⭐⭐⭐⭐⭐ Fácil (path-based) | ⭐⭐⭐ Requer LB HTTP/2 aware | ⭐⭐⭐⭐⭐ Fácil |
| Caching HTTP | ⭐⭐⭐⭐⭐ Cache-Control, ETag | ⚠️ Limitado (binário) | ⭐⭐⭐⭐ Cache por query |
| Debug com proxy | ⭐⭐⭐⭐⭐ Charles/Fiddler | ⭐⭐ Requer reflection | ⭐⭐⭐⭐⭐ Playground inspecionável |

---

## 5. Infraestrutura & Observabilidade

### 5.1 Monitoramento

| Capacidade | REST | gRPC | GraphQL |
|---|---|---|---|
| Métricas HTTP | ✅ Status codes nativos | ✅ gRPC status codes (mais granulares) | ⭐⭐ Tudo HTTP 200 (erros no body) |
| Tracing | ⭐⭐⭐⭐⭐ OpenTelemetry funciona | ⭐⭐⭐⭐⭐ Excelente (metadata binária) | ⭐⭐⭐⭐⭐ Funciona bem |
| Logging | ⭐⭐⭐⭐⭐ Texto legível | ⭐⭐⭐ Requer parsing protobuf | ⭐⭐⭐⭐ JSON legível |

> **Problema GraphQL**: Todos os requests retornam HTTP 200, mesmo com erros. Requer inspeção do payload.

### 5.2 Resiliência

| Padrão | REST | gRPC | GraphQL |
|---|---|---|---|
| Timeout/deadlines | Cliente-configurado | ✅ Context propagation nativa | Cliente-configurado |
| Retries seguros | Idempotência manual | ✅ Semantics built-in (GET vs POST) | ⚠️ Query complexity |
| Circuit breaker | External (Hystrix/Resilience4j) | ✅ Interceptors + client libs | External |
| Load balancing | DNS/Round-robin | ✅ Subchannel load balancing | DNS/Round-robin |

---

## 6. Cenários de Uso

### Quando usar cada um

| Cenário | Recomendação | Justificativa |
|---|---|---|
| **Microserviços internos** | gRPC | Performance, contratos rigorosos, streaming |
| **API pública/terceiros** | REST | Universalidade, facilidade de adoção |
| **Mobile/SPA complexo** | GraphQL | Evita over-fetching, queries flexíveis |
| **Streaming em tempo real** | gRPC | WebSocket alternativo mais simples |
| **Integração legacy** | REST | Compatibilidade universal |
| **Analytics/BI** | GraphQL | Queries ad-hoc exploratórias |

---

## 7. Trade-offs no Contexto deste POC

### Por que gRPC foi escolhido

| Requisito do POC | gRPC | REST | GraphQL |
|---|---|---|---|
| Baixa latência entre serviços | ✅ Binário eficiente | ❌ JSON overhead | ❌ JSON overhead |
| Contratos compartilhados (proto/) | ✅ Source of truth | ⚠️ OpenAPI duplicado | ⚠️ Schema separado |
| Poliglotia (Java/Kotlin/Python) | ✅ Stubs automáticos | ⚠️ Manual/geradores | ⚠️ Resolvers por linguagem |
| Streaming server-side | ✅ Nativo | ❌ SSE/WebSocket | ✅ Subscriptions |
| Deadlines propagation | ✅ Context gRPC nativo | ❌ Manual | ❌ Manual |
| Observabilidade (OTel/Jaeger) | ✅ Metadata binária eficiente | ✅ Funciona | ✅ Funciona |

### Trade-offs aceitos neste projeto

1. **Debugging mais complexo**: Usamos `grpcurl` em vez de `curl`
2. **Browser não acessa diretamente**: Arquitetura de microserviços internos (B2B)
3. **Setup inicial maior**: Requer buf/protoc, mas paga dividendos em integração

---

## 8. Matriz de Decisão

```
Precisa de:
├── Alta performance (sub-10ms)? → gRPC
├── Schema evolution sem breaking changes? → gRPC ou GraphQL
├── Suporte a browsers diretos? → GraphQL ou REST
├── Streaming bidirecional? → gRPC
├── Ferramentas universais (curl)? → REST
├── Queries flexíveis por cliente? → GraphQL
├── Contratos compartilhados entre squads? → gRPC
└── Integração rápida com terceiros? → REST
```

---

## 9. Conclusão

Não há "melhor" paradigma — há **adequação ao contexto**:

- **REST** permanece o padrão universal para APIs públicas e integrações simples
- **gRPC** domina em microserviços internos onde performance e contratos rigorosos importam
- **GraphQL** brilha em aplicações frontend complexas com necessidades de dados variáveis

Este POC demonstra gRPC em sua sweet spot: comunicação eficiente entre serviços backend heterogêneos (Java/Kotlin/Python) com observabilidade e resiliência de classe empresarial.
