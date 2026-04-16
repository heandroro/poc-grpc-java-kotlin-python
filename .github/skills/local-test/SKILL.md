---
name: local-test
description: Testes locais com Docker Compose, Testcontainers Desktop, Spring Profiles e Spring Boot DevTools
---

# Local Test

Skill para execução de testes locais em projetos Java 25 + Spring Boot 4 com ambiente de desenvolvimento reproduzível.

## Recursos Bundled

### `examples/` — Código de Exemplo
| Arquivo | Conteúdo |
|---------|----------|
| `TestcontainersConfig.java` | `@TestConfiguration` com postgres, redis, kafka reutilizáveis + `TestApplication` |

## Ferramentas obrigatórias

- **Docker Compose** — orquestração de serviços locais (banco, cache, messaging)
- **Testcontainers Desktop** — proxy para containers reutilizáveis durante desenvolvimento
- **Spring Boot DevTools** — hot reload e restart automático
- **Spring Profiles** — configuração por ambiente (`local`, `test`, `integration`)
- **Spring Boot Docker Compose Support** — integração nativa com `compose.yml`

## Configuração de profiles

- `application-local.yml` na raiz de `src/main/resources/` com `spring.docker.compose.file=compose.yml`.
- `SPRING_PROFILES_ACTIVE=local` ativado por padrão no ambiente de desenvolvimento.
- `spring.docker.compose.lifecycle-management=start-and-stop` — Spring gerencia o ciclo de vida do Compose.

## Docker Compose para desenvolvimento

- `compose.yml` na raiz do projeto: serviços `postgres`, `redis`, `kafka`.
- Imagens com tag fixa (nunca `latest`): `postgres:17`, `redis:7-alpine`, `confluentinc/cp-kafka:7.7.0`.
- Volumes nomeados para persistência de dados locais.

## Testcontainers reutilizáveis

- `@TestConfiguration` com `@Bean @ServiceConnection` — injeta propriedades dos containers automaticamente.
- `.withReuse(true)` — container persiste entre reinicializações da JVM (mais rápido no dia-a-dia).
- `TestApplication.main()` em `src/test/java` — boot local completo com todos os containers.

Ver `examples/TestcontainersConfig.java` para a configuração completa.

## Convenções

- `compose.yml` na raiz do projeto para ambiente local
- Profile `local` ativado por padrão em desenvolvimento (`SPRING_PROFILES_ACTIVE=local`)
- Dados de seed em `src/test/resources/data/` (SQL ou JSON)
- Usar `@TestConfiguration` para configurações específicas de teste
- Manter `TestApplication.java` em `src/test/java` para boot local com Testcontainers
- Scripts de inicialização de banco em `src/main/resources/db/migration/` (Flyway)
- Documentar pré-requisitos no README (Docker, Java 25, etc.)
- Um único comando para rodar localmente: `./gradlew bootRun` ou `./mvnw spring-boot:run`
