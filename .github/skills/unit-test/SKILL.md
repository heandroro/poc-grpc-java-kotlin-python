---
name: unit-test
description: Testes unitários com JUnit 6, Mockito 5, AssertJ, JaCoCo (≥90% coverage), Instancio e DataFaker
---

# Unit Test

Skill para testes unitários em projetos Java 25 + Spring Boot 4 com foco em isolamento, cobertura, e feedback rápido.

## Recursos Bundled

### `examples/` — Código de Exemplo
| Arquivo | Conteúdo |
|---------|----------|
| `OrderServiceTest.java` | JUnit 6 + Mockito + AssertJ, Instancio, DataFaker, `@Nested` |

### `scripts/` — Configurações de Build
| Arquivo | Conteúdo |
|---------|----------|
| `maven-jacoco.xml` | JaCoCo com threshold 90% linha e branch |

### `references/` — Documentação Técnica
| Arquivo | Conteúdo |
|---------|----------|
| `testing-guide.md` | Nomenclatura BDD, cobertura, Instancio, DataFaker, fixtures |

## Ferramentas obrigatórias

- **JUnit 6** — framework principal de testes unitários
- **Mockito 5** — mocking framework com suporte a `inline` mock maker
- **AssertJ** — assertions fluentes e descritivas
- **JaCoCo** — cobertura de código com threshold mínimo de 90%
- **Spring Boot Test Slices** — `@WebMvcTest`, `@DataJpaTest`, `@JsonTest` para testes focados
- **Instancio** — geração automática de objetos de teste (fixtures)
- **DataFaker** — geração de dados fake realistas (nomes, emails, endereços, etc.)

## Regras e padrões

- Nomenclatura: `should_[resultado]_when_[condição]` ou `[método]_[cenário]_[resultado]`
- Um assert lógico por teste (múltiplos asserts do mesmo conceito são aceitos)
- Testes unitários não devem acessar rede, banco, ou filesystem
- Usar `@MockitoExtension` para injeção de mocks
- Preferir `BDDMockito` (given/when/then) sobre `Mockito.when`
- Cobertura mínima por módulo: 90% de linhas, 90% de branches
- Testes devem executar em menos de 10 segundos no total do módulo

## Estrutura de teste

- `@ExtendWith(MockitoExtension.class)` + `@Mock` + `@InjectMocks` — estrutura base.
- Para controllers servlet com Spring Boot 4, preferir `MockMvcBuilders` com setup explícito do controller, mocks dos casos de uso e `setControllerAdvice(new ApiExceptionHandler())` para incluir o `@RestControllerAdvice` global.
- Padrão BDD: `given(...)` / `when` / `then(...)` com BDDMockito.
- Um assert lógico por teste; `@Nested` para agrupar cenários.
- Ver `examples/OrderServiceTest.java` para implementação completa.

## Geração de dados de teste

### Instancio — fixtures automáticas

- `Instancio.create(Foo.class)` — objeto com todos os campos populados aleatoriamente.
- `Instancio.of(Foo.class).set(field(Foo::status), value).create()` — sobrescrever campos específicos.
- `Instancio.createList(Foo.class, 10)` — coleções.
- Dependência: `instancio-junit:5.0.0` (scope `test`).

### DataFaker — dados realistas

- `faker.commerce().productName()` → `"Teclado Mecânico RGB"`.
- `faker.internet().emailAddress()` → `"ana.silva@email.com"`.
- `faker.cpf().valid()`, `faker.cnpj().valid()` para dados brasileiros.
- Centralizar geradores em classes `*Fixture` reutilizáveis.
- Dependência: `datafaker:2.3.1` (scope `test`).

## Configuração JaCoCo

Ver `scripts/maven-jacoco.xml` para configuração com threshold de 90% linha e 90% branch.

## Convenções

- Cada classe de produção deve ter uma classe de teste correspondente
- Usar `@Nested` para organizar cenários complexos
- Fixtures compartilhados via métodos factory (não herança)
- **Usar Instancio para objetos de teste complexos** — evitar construção manual
- **Usar DataFaker para dados realistas** — evitar "foo", "bar", "test", "123"
- Criar classes `*Fixture` centralizadas para geração de dados do domínio
- Testes flaky devem ser isolados e corrigidos imediatamente
- Executar testes unitários no CI em cada push
