# Windsurf Configuration Guide

Guia completo da configuração do Windsurf para o projeto poc-grpc (Python, Kotlin, Java).

## Overview

Este projeto utiliza o Windsurf com configurações distribuídas em múltiplos níveis para suportar desenvolvimento em três linguagens (Python 3.12, Kotlin 1.9, Java 25) com gRPC.

## Estrutura de Configuração

```
.windsurf/
├── rules/                    # Regras ativas (auto-detecção)
│   ├── test-agent.md
│   ├── devops-agent.md
│   ├── quality-agent.md
│   └── performance-agent.md
├── skills/                   # Habilidades invocáveis (@mention)
│   ├── unit-test/
│   ├── best-practices/
│   ├── integration-test/
│   └── ... (11 skills)
└── workflows/                # Workflows do projeto
    └── docs.md

.github/
└── instructions/
    └── java-spring-boot.instructions.md  # Regras always-on para Java

AGENTS.md                     # Regras compiladas (gerado por APM CLI)
```

## Rules (`.windsurf/rules/`)

Rules são **automaticamente detectadas** pelo Windsurf e ativadas conforme o contexto.

| Rule | Trigger | Descrição |
|------|---------|-----------|
| `test-agent.md` | `model_decision` | Estratégia de testes (unit, integration, arch) |
| `devops-agent.md` | `model_decision` | CI/CD, containers, Kubernetes |
| `quality-agent.md` | `model_decision` | Qualidade de código, boas práticas |
| `performance-agent.md` | `model_decision` | Performance, tuning, profiling |

### Como Funciona

- **Trigger `model_decision`**: Apenas a `description` é mostrada no prompt. O conteúdo completo é carregado quando o Cascade decide que é relevante.
- **Ativação**: Automática por contexto ou manual via `@rule-name`.

## Skills (`.windsurf/skills/`)

Skills são **invocáveis manualmente** via `@skill-name` no chat.

| Skill | Conteúdo |
|-------|----------|
| `@unit-test` | JUnit 6, Mockito, AssertJ, JaCoCo (≥90% coverage) |
| `@best-practices` | SOLID, Clean Code, Rich Domain, MapStruct |
| `@integration-test` | Testcontainers, WireMock, Spring Boot Test |
| `@arch-test` | ArchUnit, regras de dependência |
| `@container` | Dockerfile, Jib, Buildpacks, health probes |
| `@code-quality` | Checkstyle, SpotBugs, PMD, SonarQube |
| `@observability` | Micrometer, Prometheus, OpenTelemetry |
| `@stress-test` | Gatling, JMH, k6 |
| `@tuning` | JVM flags, Virtual Threads, caching |
| `@local-test` | Docker Compose, Testcontainers Desktop |
| `@apm-java-25-spring-boot-4-hexagonal` | Stack completa APM |

### Estrutura de uma Skill

```
.windsurf/skills/unit-test/
├── SKILL.md              # Arquivo principal com frontmatter
├── examples/             # Código de exemplo
├── references/           # Documentação técnica
└── scripts/              # Configurações de build
```

## Instructions (`.github/instructions/`)

Arquivos `*.instructions.md` são **sempre aplicados** automaticamente pelo Windsurf para arquivos correspondentes.

### `java-spring-boot.instructions.md`

- **Aplica-se a**: `**/*.java`
- **Conteúdo**: Regras Java 25 + Spring Boot 4
- **Modo**: Always on (não precisa invocar)

Principais diretrizes:
- Records para DTOs
- Constructor injection (nunca `@Autowired` em campos)
- Virtual Threads habilitados
- Hexagonal architecture
- JaCoCo coverage ≥ 80%

## AGENTS.md (Raiz)

Arquivo **gerado automaticamente** pelo APM CLI (`specify apm compile`).

- Compila todas as instruções distribuídas em `.github/`
- Atualizado automaticamente quando há mudanças
- **Não edite manualmente**

## Como Usar

### 1. Desenvolvimento Java

Ao editar arquivos `.java`, o Windsurf automaticamente aplica:
- Regras de `java-spring-boot.instructions.md`
- Convenções de `AGENTS.md`

### 2. Criar Testes

No chat, mencione:
```
@test-agent gere testes unitários para NotificationService
```

Ou use a skill diretamente:
```
@unit-test crie testes para AnalyticsGrpcAdapter
```

### 3. Revisar Código

```
@quality-agent revise este PR para conformidade Java 25
```

### 4. Trabalhar com Containers

```
@devops-agent crie um Dockerfile multi-stage para o notification-service
```

### 5. Acessar Documentação Técnica

```
@best-practices mostre exemplos de records vs classes
```

## Triggers de Activation

| Trigger | Descrição | Uso |
|---------|-----------|-----|
| `always_on` | Sempre no system prompt | Global rules, AGENTS.md |
| `model_decision` | Description visível, conteúdo on-demand | Agents especializados |
| `glob` | Aplica-se a arquivos específicos | Por linguagem/framework |
| `manual` | Só ativa com @mention | Referências rápidas |

## Dicas

- **Rules** = comportamento automático (contexto)
- **Skills** = conhecimento técnico (sob demanda)
- **Instructions** = regras por tipo de arquivo
- **Workflows** = sequências de passos (`.windsurf/workflows/`)

## Troubleshooting

### Rules não aparecem
- Verifique se estão em `.windsurf/rules/*.md`
- Confirme o frontmatter YAML está correto
- Reinicie o Windsurf

### Skills não são encontradas
- Skills devem estar em `.windsurf/skills/<name>/SKILL.md`
- Invocar com `@skill-name` (nome do diretório)

### Java instructions não aplicam
- Verifique `.github/instructions/java-spring-boot.instructions.md` existe
- Confirme o arquivo tem `applyTo: "**/*.java"`
- Regenere com `specify apm compile` se necessário

---

*Última atualização: Abril 2026*
