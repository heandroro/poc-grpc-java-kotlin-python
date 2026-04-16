---
name: integration-test
description: Testes de integração com Testcontainers, WireMock, Spring Boot Test, REST Assured e WebTestClient
---

# Integration Test

Skill para testes de integração em projetos Java 25 + Spring Boot 4 com infraestrutura real via containers.

## Recursos Bundled

### `examples/` — Código de Exemplo
| Arquivo | Conteúdo |
|---------|----------|
| `IntegrationTestExamples.java` | `@SpringBootTest` + Testcontainers, WireMock, `@DataJpaTest` com `@ServiceConnection` |

## Ferramentas obrigatórias

- **Testcontainers** — containers efêmeros para PostgreSQL, Redis, Kafka, etc.
- **WireMock** — mock de APIs HTTP externas
- **Spring Boot Test** — `@SpringBootTest` com contexto real
- **REST Assured** ou **WebTestClient** — testes de API HTTP
- **Awaitility** — assertions assíncronas com timeout

## Regras e padrões

- Testes de integração devem usar o profile `integration-test`
- Separar testes de integração dos unitários via tag `@Tag("integration")`
- Usar `@DynamicPropertySource` para injetar propriedades de containers
- Cada teste deve ser independente — sem ordem de execução
- Usar `@Transactional` para rollback automático em testes de banco
- Tempo máximo de execução por teste: 30 segundos
- Containers devem ser compartilhados via `@Container` static ou `@ServiceConnection`

## Estrutura de teste

- `@SpringBootTest(webEnvironment = RANDOM_PORT)` — sobe contexto Spring completo na porta aleatória.
- `@Container` + `@ServiceConnection` — injeta propriedades do container automaticamente (sem `@DynamicPropertySource`).
- `static` container — compartilhado entre todos os testes da classe.
- `MockMvc` para testes servlet-layer com setup explícito via `MockMvcBuilders` e inclusão do `@RestControllerAdvice` global no setup.
- `WebTestClient` ou cliente HTTP real para smoke/integration HTTP fim a fim.
- `@DataJpaTest` para testar repositórios com slice mínimo do contexto.

## WireMock para APIs externas

- `@WireMockTest(httpPort = 8089)` — sobe WireMock local para a porta configurada.
- `stubFor(post(...).willReturn(...))` — configura resposta simulada.
- Testar cenários de erro (5xx, timeout) além do caminho feliz.

Ver `examples/IntegrationTestExamples.java` para implementação completa.

## Convenções

- Manter `src/test/resources/` com dados de teste (fixtures JSON, SQL scripts)
- Usar `@Sql` para setup de dados quando necessário
- Containers devem usar imagens com tag fixa (não `latest`)
- Testes de integração rodam em stage separado no CI pipeline
- Documentar dependências externas de cada teste (qual container, qual serviço)
