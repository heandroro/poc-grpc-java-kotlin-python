---
name: container
description: Containerização com Dockerfile multi-stage, Jib, Cloud Native Buildpacks, GraalVM Native e health probes
---

# Container

Skill para containerização de aplicações Java 25 + Spring Boot 4.

## Recursos Bundled

### `examples/` — Código de Exemplo
| Arquivo | Conteúdo |
|---------|----------|
| `Dockerfile-multi-stage` | Multi-stage build com layer extraction e non-root user |
| `Dockerfile-native` | GraalVM Native Image + Distroless (~50MB, startup < 100ms) |
| `kubernetes-probes.yml` | Health probes Spring Boot + Kubernetes + `.dive-ci.yaml` |

### `scripts/` — Configurações de Build
| Arquivo | Conteúdo |
|---------|----------|
| `maven-jib.xml` | Jib Maven plugin com ZGC flags e non-root user |

### `references/` — Documentação Técnica
| Arquivo | Conteúdo |
|---------|----------|
| `dockerfile-guide.md` | Boas práticas, multi-stage, health probes, alternativas Jib/Buildpacks |

## Abordagens de build

- **Multi-stage Dockerfile** — controle total sobre o build
- **Jib** — build de container sem Dockerfile (Maven/Gradle plugin)
- **Cloud Native Buildpacks** — `spring-boot:build-image` com Paketo
- **GraalVM Native + Distroless** — imagem mínima para produção

## Multi-stage Dockerfile

- 3 stages: `builder` (compila), `extractor` (extrai layers), runtime (`eclipse-temurin:25-jre`).
- `java -Djarmode=tools -jar app.jar extract --layers` — separa depêndencias da aplicação para cache otimizado.
- Non-root user: `addgroup --system app && adduser --system --ingroup app app`.
- JVM flags no `ENTRYPOINT`: `-XX:+UseZGC -XX:MaxRAMPercentage=75.0`.

Ver `examples/Dockerfile-multi-stage` e `references/dockerfile-guide.md`.

## Jib (sem Dockerfile)

- Build de container sem Docker daemon — ideal para CI.
- Cria layers determinísticas e otimizadas automaticamente.
- Ver `scripts/maven-jib.xml` para configuração completa.

## Cloud Native Buildpacks

- Maven: `./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=registry.example.com/app:latest`
- Gradle: `tasks.named<BootBuildImage>("bootBuildImage") { imageName.set(...) }`
- Env vars: `BP_JVM_VERSION=25`, `BP_NATIVE_IMAGE=true` para native.

## GraalVM Native + Distroless

Ver `examples/Dockerfile-native` — imagem ~50MB, startup < 100ms.

## Health Probes (Kubernetes)

- Habilitar no `application.yml`: `management.endpoint.health.probes.enabled=true`.
- Grupos: `liveness` inclui `livenessState`; `readiness` inclui `readinessState,db,redis`.
- Ver `examples/kubernetes-probes.yml` para configuração Spring Boot + Kubernetes deployment.

## Análise de imagem com Dive

- `brew install dive` + `dive registry.example.com/app:latest` para inspecionar layers localmente.
- Em CI: `dive build -t app:latest . --ci` com `.dive-ci.yaml` (ver `examples/kubernetes-probes.yml`).

### O que o dive revela

| Métrica | Significado | Target |
|---------|-------------|--------|
| **Image efficiency** | Quão bem aproveitadas são as camadas | ≥ 95% |
| **Wasted space** | Dados duplicados ou removidos em camadas posteriores | < 50MB |
| **Layer size** | Tamanho individual de cada camada | < 100MB cada |

### Otimizações com Dive

- JARs de aplicação na última camada (frequentemente modificada); dependências em camadas anteriores (cache-friendly).
- `RUN apt-get ... && rm -rf /var/lib/apt/lists/*` em único `RUN` — arquivos deletados em camadas posteriores ainda ocupam espaço.
- CI/CD: `wagoodman/dive-action@v1` com `config-file: .dive-ci.yaml` (ver `examples/kubernetes-probes.yml`).

## Boas práticas de imagem

- Usar imagens base oficiais com tag fixa (ex: `eclipse-temurin:25-jre`)
- Non-root user dentro do container
- Multi-stage build para minimizar tamanho da imagem
- Labels OCI (`org.opencontainers.image.*`)
- `.dockerignore` para excluir arquivos desnecessários
- Scan de vulnerabilidades com Trivy ou Snyk
- Limitar recursos no deploy: CPU/memory requests e limits
- **Executar dive antes de push para registry** — garantir eficiência ≥ 95%

## Convenções

- Imagens taggeadas com semver + git SHA (ex: `1.0.0-abc1234`)
- Registry privado para imagens internas
- CI pipeline: build → test → scan → push → deploy
- Manter Dockerfile na raiz do projeto
- Documentar variáveis de ambiente no README e no Dockerfile
