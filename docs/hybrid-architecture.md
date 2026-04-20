# Arquitetura Híbrida Ideal: gRPC + REST

Documento de arquitetura propondo uma abordagem híbrida que combina gRPC e REST de forma estratégica, otimizando custos, manutenção e performance para diferentes camadas do sistema.

---

## 1. Visão Geral da Arquitetura Híbrida

### 1.1 Princípios

1. **Use o protocolo certo para o job certo** — não force tudo em um único paradigma
2. **Edge = REST** — APIs públicas e browsers usam REST/WebSocket
3. **Core = gRPC** — Comunicação service-to-service usa gRPC
4. **Custo consciente** — Evite serviços caros de gateway quando possível
5. **Manutenção simplificada** — Reduza camadas de tradução desnecessárias

### 1.2 Diagrama da Arquitetura

```mermaid
flowchart TB
    subgraph Edge["Edge Layer (REST/WebSocket)"]
        direction TB
        BFF["BFF (Backend-for-Frontend)<br/>FastAPI / Spring Boot Gateway"]
        WS["WebSocket Manager<br/>Socket.IO / SignalR"]
        APIGW["API Gateway (opcional)<br/>Kong / Ambassador"]
    end
    
    subgraph Core["Core Layer (gRPC)"]
        direction TB
        NS["notification-service<br/>Java + gRPC"]
        SS["subscription-service<br/>Kotlin + gRPC"]
        AS["analytics-service<br/>Python + gRPC"]
        MESH["Service Mesh (opcional)<br/>Istio / Linkerd"]
    end
    
    subgraph Data["Data Layer"]
        DB1[(PostgreSQL)]
        DB2[(Redis)]
        TS[(TimescaleDB)]
    end
    
    subgraph Clients["Clientes"]
        WEB["Web / SPA<br/>React/Vue"]
        MOBILE["Mobile<br/>iOS/Android"]
        B2B["B2B API Clients<br/>API Key"]
    end
    
    %% Fluxos
    WEB -->|HTTPS / REST| APIGW
    MOBILE -->|HTTPS / REST| APIGW
    B2B -->|HTTPS / REST| APIGW
    
    APIGW -->|REST| BFF
    BFF -->|gRPC| NS
    BFF -->|gRPC| SS
    BFF -->|gRPC| AS
    
    WS -->|gRPC| NS
    
    NS -->|fan-out gRPC| SS
    SS -->|gRPC| NS
    NS -->|gRPC| AS
    
    NS --> DB1
    SS --> DB1
    AS --> TS
    NS --> DB2
    SS --> DB2
    
    style Edge fill:#e1f5fe
    style Core fill:#fff3e0
    style BFF fill:#c8e6c9
```

---

## 2. Separação de Responsabilidades

### 2.1 Matriz de Decisão: REST vs gRPC

```mermaid
flowchart TD
    Start([Requisição]) --> Public{API Pública?}
    
    Public -->|Sim| REST1[Use REST]
    Public -->|Não| Browser{Browser/SPA?}
    
    Browser -->|Sim| REST2[REST + WebSocket<br/>para streaming]
    Browser -->|Não| Internal{Service-to-Service?}
    
    Internal -->|Sim| Performance{Alto Throughput<br/>ou Streaming?}
    Internal -->|Não| REST3[REST para simplicidade]
    
    Performance -->|Sim| GRPC1[Use gRPC]
    Performance -->|Não| REST4[REST é suficiente]
    
    style GRPC1 fill:#c8e6c9
    style REST1 fill:#ffccbc
    style REST2 fill:#ffccbc
```

### 2.2 Responsabilidades por Protocolo

| Aspecto | REST (Edge) | gRPC (Core) |
|---|---|---|
| **Autenticação** | OAuth 2.0 / JWT / API Keys | mTLS + JWT metadata |
| **Rate Limiting** | Por client (API Gateway) | Por serviço (service mesh) |
| **Payload** | JSON (legível, debugável) | Protobuf (eficiente) |
| **Caching** | HTTP Cache-Control, ETag | Application-level caching |
| **Observabilidade** | Access logs, HTTP metrics | gRPC metrics, distributed tracing |
| **Versioning** | URL / Header versioning | Protobuf evolution |

---

## 3. Componentes da Arquitetura

### 3.1 BFF (Backend-for-Frontend)

**Propósito:** Camada de tradução entre REST público e gRPC interno.

```mermaid
flowchart LR
    subgraph BFF["BFF Layer (1 por frontend)"]
        REST_IN["REST Controller<br/>@RestController"]
        GRPC_OUT["gRPC Client<br/>Stub"]
        CACHE["Cache<br/>Caffeine / Redis"]
    
        REST_IN -->|1. Recebe HTTP| CACHE
        CACHE -->|2. Miss| GRPC_OUT
        GRPC_OUT -->|3. gRPC call| CORE["Core Services"]
        CORE -->|4. Response| GRPC_OUT
        GRPC_OUT -->|5. Transform| REST_IN
    end
    
    style BFF fill:#e1f5fe
```

**Implementação (Spring Boot):**

```java
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationBffController {
    
    @Autowired
    private NotificationServiceGrpc.NotificationServiceBlockingStub grpcClient;
    
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> list(
            @RequestHeader("Authorization") String token,
            @RequestParam String userId) {
        
        // Transforma REST em gRPC
        StreamNotificationsRequest request = StreamNotificationsRequest.newBuilder()
            .setUserId(userId)
            .setMinPriority(NotificationPriority.NORMAL)
            .build();
        
        // Chama serviço gRPC
        Iterator<Notification> responses = grpcClient.streamNotifications(
            request,
            createMetadata(token)  // JWT forwarding
        );
        
        // Transforma gRPC em REST
        List<NotificationDTO> dtos = new ArrayList<>();
        responses.forEachRemaining(n -> dtos.add(toDTO(n)));
        
        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS))
            .body(dtos);
    }
}
```

**Vantagens do BFF:**
- **Caching HTTP** no edge reduz chamadas gRPC internas
- **Agregação** de múltiplos serviços em um endpoint REST
- **Transformação** de payload (Protobuf ↔ JSON) em um ponto só
- **Versioning REST** sem afetar serviços core

### 3.2 Service Mesh (Opcional)

Quando usar: >10 serviços, necessidade de mTLS automático, canary deployments.

```mermaid
flowchart TB
    subgraph Mesh["Service Mesh (Istio)"]
        Proxy1["Envoy Sidecar<br/>notification-service"]
        Proxy2["Envoy Sidecar<br/>subscription-service"]
        Proxy3["Envoy Sidecar<br/>analytics-service"]
        Control["Istiod<br/>Control Plane"]
    end
    
    NS["notification-service<br/>Port: 50051"]
    SS["subscription-service<br/>Port: 50052"]
    AS["analytics-service<br/>Port: 50053"]
    
    NS <-->|mTLS| Proxy1
    SS <-->|mTLS| Proxy2
    AS <-->|mTLS| Proxy3
    
    Proxy1 <-->|gRPC| Proxy2
    Proxy2 <-->|gRPC| Proxy3
    Proxy1 <-->|gRPC| Proxy3
    
    Control --> Proxy1
    Control --> Proxy2
    Control --> Proxy3
    
    style Mesh fill:#f3e5f5
```

---

## 4. Análise de Custos (Híbrido vs Monolítico)

### 4.1 Custo por Arquitetura

Estimativa para: 10M notificações/dia, 3 serviços, 3 AZs, 500 conexões simultâneas.

```mermaid
pie title Custo Mensal Comparativo (AWS)
    "Monolítico gRPC puro" : 327
    "Monolítico REST puro" : 287
    "Híbrido (BFF + gRPC Core)" : 340
    "Híbrido com API Gateway" : 520
```

| Arquitetura | Componentes | Custo AWS/mês | Custo GCP/mês | Custo Azure/mês |
|---|---|---|---|---|
| **gRPC puro** | EKS + NLB + X-Ray | $327 | $258 | $280 |
| **REST puro** | ECS + ALB + ElastiCache | $287 | $240 | $376 |
| **Híbrido** | EKS + 1 ALB (BFF) + NLB (core) + Redis | $340 | $275 | $320 |
| **Híbrido + Kong** | EKS + Kong + NLB + Redis | $380 | $315 | $360 |

### 4.2 Justificativa do Custo Adicional do Híbrido

O híbrido custa ~5-15% a mais que puro, mas entrega:

| Benefício | Valor |
|---|---|
| **Caching no edge** | Reduz 30-50% das chamadas gRPC |
| **BFF por cliente** | Mobile e Web podem ter APIs otimizadas |
| **Debuggabilidade** | REST no edge permite curl/browser |
| **Segurança** | mTLS no core, OAuth no edge |

**ROI:** O custo extra do ALB é compensado pela redução de chamadas gRPC (caching) e pela facilidade de debugging.

---

## 5. Manutenção e Operação

### 5.1 Complexidade Operacional

```mermaid
flowchart LR
    subgraph Complexity["Complexidade de Manutenção"]
        direction TB
        
        GRPC["gRPC Puro<br/>⚠️ Proto changes<br/>⚠️ Tooling específico<br/>✅ Um protocolo só"]
        
        REST["REST Puro<br/>✅ Debugging fácil<br/>✅ Tooling universal<br/>⚠️ Contratos soltos"]
        
        HYBRID["Híbrido<br/>⚠️ Duas stacks<br/>⚠️ BFF para manter<br/>✅ Melhor dos dois mundos<br/>✅ Isolamento de mudanças"]
    end
    
    GRPC --> HYBRID --> REST
    
    style HYBRID fill:#fff9c4
```

### 5.2 Estratégia de Deploy

| Mudança | Impacto | Estratégia |
|---|---|---|
| **Atualizar proto** | Core services | Canary deployment via service mesh |
| **Mudar REST API** | BFF apenas | Blue/green no BFF, core intacto |
| **Novo endpoint** | BFF + Core | Proto primeiro, BFF depois |
| **Bug no core** | Rollback rápido | Rollback gRPC sem afetar REST |

### 5.3 Monitoramento

```mermaid
flowchart TB
    subgraph Observability["Observabilidade Híbrida"]
        EDGE["Edge (REST)"]
        CORE["Core (gRPC)"]
        TRACE["Distributed Tracing<br/>OpenTelemetry"]
        
        subgraph EdgeMetrics["Métricas REST"]
            E1["Latency P99"]
            E2["Cache Hit Ratio"]
            E3["HTTP 5xx Rate"]
        end
        
        subgraph CoreMetrics["Métricas gRPC"]
            C1["RPC Latency"]
            C2["gRPC Status Codes"]
            C3["Fan-out Success Rate"]
        end
        
        EDGE --> TRACE
        CORE --> TRACE
        EDGE --> EdgeMetrics
        CORE --> CoreMetrics
    end
    
    style EdgeMetrics fill:#e1f5fe
    style CoreMetrics fill:#fff3e0
```

**Stack recomendada:**
- **Edge:** Prometheus + Grafana (métricas HTTP)
- **Core:** OpenTelemetry + Jaeger (tracing gRPC)
- **Unified:** ELK ou Loki para logs estruturados

---

## 6. Implementação Prática

### 6.1 Estrutura de Código

```
notification-system/
├── edge/
│   ├── web-bff/                 # Spring Boot/FastAPI
│   │   ├── src/
│   │   │   ├── rest/           # Controllers REST
│   │   │   └── grpc/           # Client stubs gRPC
│   │   └── proto/              # Proto files (submodule)
│   └── mobile-bff/             # BFF otimizado para mobile
│
├── core/
│   ├── notification-service/   # Java + gRPC
│   ├── subscription-service/   # Kotlin + gRPC
│   └── analytics-service/      # Python + gRPC
│
├── infra/
│   ├── k8s/                    # Kubernetes manifests
│   ├── terraform/              # IaC para AWS/GCP/Azure
│   └── istio/                  # Service mesh config (opcional)
│
└── proto/                      # Contratos compartilhados
    ├── notification/v1/
    ├── subscription/v1/
    └── analytics/v1/
```

### 6.2 Deploy no Kubernetes

```yaml
# BFF Deployment
apiVersion: apps/v1
kind: Deployment
metadata:
  name: notification-bff
spec:
  replicas: 2
  template:
    spec:
      containers:
      - name: bff
        image: notification-bff:1.0.0
        ports:
        - containerPort: 8080  # REST
        env:
        - name: NOTIFICATION_SERVICE_HOST
          value: "notification-service.core.svc.cluster.local:50051"
        - name: SUBSCRIPTION_SERVICE_HOST
          value: "subscription-service.core.svc.cluster.local:50052"
---
# Core Service
apiVersion: apps/v1
kind: Deployment
metadata:
  name: notification-service
  namespace: core
spec:
  replicas: 3
  template:
    spec:
      containers:
      - name: service
        image: notification-service:1.0.0
        ports:
        - containerPort: 50051  # gRPC
        - containerPort: 8080   # Health/metrics
```

### 6.3 Service Discovery

```mermaid
flowchart TB
    subgraph K8s["Kubernetes Cluster"]
        DNS["CoreDNS<br/>svc.cluster.local"]
        
        subgraph EdgeNS["namespace: edge"]
            BFF_POD["BFF Pod"]
            BFF_SVC["BFF Service<br/>ClusterIP"]
        end
        
        subgraph CoreNS["namespace: core"]
            NS_POD["notification-service Pod"]
            NS_SVC["notification-service<br/>Headless Service"]
        end
        
        BFF_POD -->|gRPC| BFF_SVC
        BFF_SVC -->|gRPC| NS_SVC
        NS_SVC --> NS_POD
        
        DNS -.->|resolve| NS_SVC
    end
    
    LB["External Load Balancer<br/>ALB/GLB"]
    LB -->|HTTPS| BFF_SVC
    
    style EdgeNS fill:#e1f5fe
    style CoreNS fill:#fff3e0
```

---

## 7. Roadmap de Implementação

### Fase 1: Foundation (Semana 1-2)
- [ ] Setup Kubernetes cluster (EKS/GKE/AKS)
- [ ] Deploy core services com gRPC
- [ ] Service mesh básico (opcional)

### Fase 2: Edge Layer (Semana 3-4)
- [ ] Implementar BFF (1 por cliente principal)
- [ ] Configurar ALB/GLB para REST
- [ ] Setup caching (Redis)

### Fase 3: Observabilidade (Semana 5)
- [ ] OpenTelemetry collector
- [ ] Jaeger para tracing
- [ ] Grafana dashboards (REST + gRPC)

### Fase 4: Produção (Semana 6)
- [ ] Rate limiting no edge
- [ ] mTLS no core
- [ ] Canary deployments

---

## 8. Conclusão

A arquitetura híbrida **gRPC (core) + REST (edge)** oferece:

| Aspecto | Avaliação |
|---|---|
| **Performance** | ⭐⭐⭐⭐⭐ gRPC no core entrega máxima eficiência |
| **Custo** | ⭐⭐⭐⭐☆ ~10% mais caro que puro, mas com ROI positivo |
| **Manutenção** | ⭐⭐⭐⭐☆ Isolamento de mudanças compensa duas stacks |
| **Debuggabilidade** | ⭐⭐⭐⭐⭐ REST no edge permite debugging rápido |
| **Escalabilidade** | ⭐⭐⭐⭐⭐ Cada camada escala independentemente |

**Recomendação final:** Adote o híbrido se você tem múltiplos clientes (Web, Mobile, B2B) e precisa de performance interna sem sacrificar debuggabilidade. O investimento no BFF paga dividendos em isolamento de mudanças e caching eficiente.
