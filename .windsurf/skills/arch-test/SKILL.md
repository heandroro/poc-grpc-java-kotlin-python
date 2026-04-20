---
name: arch-test
description: Testes de arquitetura com ArchUnit — hexagonal architecture, regras de dependência e camadas
---

# Architecture Test

Skill para validação automatizada de regras arquiteturais em projetos Java 25 + Spring Boot 4.

## Recursos Bundled

### `examples/` — Código de Exemplo
| Arquivo | Conteúdo |
|---------|----------|
| `ArchitectureTest.java` | Regras ArchUnit: dependências entre camadas, localização de classes, sem cycles |

## Ferramentas obrigatórias

- **ArchUnit** — testes de arquitetura como código
- **jMolecules** (opcional) — annotations para expressar conceitos DDD

## Regras arquiteturais

### Camadas e dependências

- **Hexagonal / Ports & Adapters**: domain não depende de infrastructure ou adapter
- **Clean Architecture**: entities e use cases não importam frameworks
- Controllers dependem apenas de services/use cases
- Services não dependem de controllers
- Repository interfaces ficam no domain; implementações no infrastructure

### Convenções de pacotes

```
com.example.app/
├── domain/           # Entidades, value objects, repository interfaces, domain services
├── application/      # Use cases, DTOs de entrada/saída, ports
├── infrastructure/   # Implementações de repositório, clients HTTP, messaging
└── adapter/
    ├── web/          # Controllers REST, request/response DTOs
    ├── persistence/  # JPA entities, Spring Data repositories
    └── messaging/    # Kafka/RabbitMQ listeners e producers
```

## Regras ArchUnit

- `domain` não depende de `infrastructure` nem de `adapter`.
- `application` não depende de `infrastructure` nem de `adapter.web`.
- `@RestController` somente em `..adapter.web..`.
- `@Entity` somente em `..adapter.persistence..`.
- `@Configuration` somente em `..infrastructure..`.
- Domínio sem imports Spring (`org.springframework..`).
- Sem dependências cíclicas entre packages.
- `Architectures.onionArchitecture()` valida toda a estrutura hexagonal de uma vez.

Ver `examples/ArchitectureTest.java` para implementação completa de todos os testes.


## Convenções

- Testes de arquitetura rodam junto com testes unitários no CI
- Novas regras devem ser adicionadas antes de implementar mudanças estruturais
- ArchUnit ruleset vive em `src/test/java/.../architecture/`
- Freezing de violations permitido apenas para migração gradual (com data de expiração)
