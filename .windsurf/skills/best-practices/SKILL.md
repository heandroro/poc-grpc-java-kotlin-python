---
name: best-practices
description: Padrões e boas práticas para projetos Java 25 + Spring Boot 4 — SOLID, Clean Code, Rich Domain, MapStruct, Streams, Virtual Threads, JSpecify
---

# Best Practices

Meta-guia de padrões e boas práticas para projetos Java 25 + Spring Boot 4. Esta skill fornece diretrizes, exemplos de código e documentação de referência organizados em subdiretórios.

## Como Usar Esta Skill

1. Consulte os **guidelines** abaixo para diretrizes rápidas.
2. Veja **`examples/`** para código completo e funcionando.
3. Leia **`references/`** para documentação técnica detalhada.
4. Utilize os **`scripts/`** para configurações de build prontas.

## Recursos Bundled

### `examples/` — Código de Exemplo
| Arquivo | Conteúdo |
|---------|----------|
| `RecordsAndSealed.java` | Records, Sealed classes, Pattern Matching |
| `SolidPrinciples.java` | Aplicação prática de SOLID |
| `CleanCode.java` | Antes/depois de refatorações Clean Code |
| `RichDomainModel.java` | Anemic vs Rich Domain Model |
| `OrderMapper.java` | @Mapper, @Named, @ValueMapping, PUT/PATCH com @MappingTarget |
| `StreamsExamples.java` | Boas e más práticas com Stream API |
| `VirtualThreads.java` | Virtual Threads e Structured Concurrency |
| `RaceConditionsExamples.java` | AtomicInteger, ReentrantLock, StampedLock, imutabilidade |
| `RecordVsClass.java` | DTOs (record) vs Entities/Aggregates (class) |
| `ExceptionsExamples.java` | Hierarquia DomainException e anti-patterns |
| `GenericsExamples.java` | Result\<T\>, Page\<T\>, Cache\<K,V\>, PECS, CQRS |

### `references/` — Documentação Técnica
| Arquivo | Conteúdo |
|---------|----------|
| `record-vs-class-guide.md` | Quando usar Record vs Class — DTOs, VOs, Entities |
| `generics-guide.md` | Bounded types, wildcards, PECS, Result\<T\>, Page\<T\> |
| `solid-principles.md` | Guia detalhado de princípios SOLID |
| `clean-code-guide.md` | Manual de código limpo e testabilidade |
| `rich-domain-guide.md` | Modelagem de domínio rico e agregados |
| `mapstruct-guide.md` | Configuração e uso avançado de MapStruct |
| `streams-guide.md` | Guia completo da Stream API |
| `threads-concurrency-guide.md` | Concorrência moderna com Virtual Threads |
| `race-conditions-guide.md` | Prevenção de condições de corrida |
| `exceptions-guide.md` | Hierarquia de exceções e Problem Detail |
| `deprecated-apis-guide.md` | Guia de migração de APIs legadas |
| `jspecify-nullaway.md` | Null safety em tempo de compilação |

### `scripts/` — Configurações de Build
| Arquivo | Conteúdo |
|---------|----------|
| `maven-dependencies.xml` | Snippets Maven (JSpecify, NullAway, MapStruct) |
| `gradle-dependencies.gradle` | Snippets Gradle (plugins e dependências) |

## Guidelines Gerais

### Java 25 Idioms
- **Records** para DTOs, Value Objects, eventos e `@ConfigurationProperties`.
- **Generics** para repositórios, handlers e tipos de retorno reutilizáveis — ver `references/generics-guide.md`.
- **Classes** para Entities JPA, Aggregates e objetos com estado mutável — ver `references/record-vs-class-guide.md`.
- **Sealed Interfaces** para domínios fechados e exaustividade.
- **Pattern Matching** para lógica de negócio expressiva.
- **Virtual Threads** para escalabilidade de I/O.
- **Scoped Values** em substituição ao `ThreadLocal`.

### Spring Boot 4 Patterns
- **Constructor Injection** obrigatório (evitar `@Autowired`).
- **Component Scan Primeiro**: prefira estereótipos do Spring a registrar casos de uso manualmente via `@Bean`.
- **Use Cases = `@Component`** quando precisarem ser gerenciados pelo Spring.
- **`@Repository`** apenas para adapters de persistência.
- **`@Bean`** apenas para objetos transversais ou de infraestrutura, como `Clock`, encoders, clients e factories.
- **RestClient** como padrão para chamadas HTTP síncronas.
- **ProblemDetail** para respostas de erro padronizadas (RFC 9457).
- **Observabilidade** nativa com Micrometer e OTel.

### Arquitetura Hexagonal
- **Domain**: Lógica pura, sem dependências de frameworks.
- **Application**: Casos de uso e orquestração. Pode usar `@Component` para integração com DI, sem assumir responsabilidades de adapter.
- **Infrastructure**: Implementações técnicas (JPA, Clients).
- **Adapters**: Entrada e saída (REST, Messaging).

### Fronteiras de Controllers
- Separe controllers REST por **recurso**, **subrecurso** ou **workflow HTTP** quando a classe começar a misturar responsabilidades.
- Não crie um controller por caso de uso apenas para reduzir construtor; o corte deve refletir a fronteira da API.
- Preserve as rotas públicas existentes ao extrair controllers menores.

### Registro de Beans
- Não centralize dezenas de casos de uso em uma única classe `@Configuration`.
- Se o tipo já está sob o pacote escaneado pela aplicação e usa apenas injeção por construtor, prefira anotá-lo corretamente e deixar o container resolvê-lo.
- Revise classes `@Configuration` periodicamente para garantir que elas exponham apenas beans que realmente exigem instanciação explícita.

## Convenções de Projeto

| Área | Diretriz | Referência |
|------|----------|------------|
| **SOLID** | SRP, OCP, LSP, ISP, DIP | `references/solid-principles.md` |
| **Clean Code** | Nomenclatura, funções curtas | `references/clean-code-guide.md` |
| **Rich Domain** | Comportamento nas entidades | `references/rich-domain-guide.md` |
| **Null Safety** | JSpecify + NullAway | `references/jspecify-nullaway.md` |
| **Exceções** | DomainException + RFC 9457 | `references/exceptions-guide.md` |
| **Mapeamento** | MapStruct (sem mapeamento manual) | `references/mapstruct-guide.md` |
| **Concorrência** | Virtual Threads + Locks seguros | `references/threads-concurrency-guide.md` |

## APIs Depreciadas — NÃO USAR
- Não use `@Autowired` em campos.
- Não use `RestTemplate` (use `RestClient`).
- Não use `ThreadLocal` com Virtual Threads (use `ScopedValue`).
- Não use `synchronized` blocks (prefira `ReentrantLock`).
- Não use `Date`/`Calendar` (use `java.time`).
- Não use `javax.*` (use `jakarta.*`).

*Para detalhes e exemplos de migração, consulte `references/deprecated-apis-guide.md`.*
