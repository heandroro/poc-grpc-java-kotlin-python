# APIs e Implementações Depreciadas — Guia Técnico

Guia completo de APIs depreciadas em Java 25 + Spring Boot 4 e seus substitutos modernos.

## Spring Framework / Boot

| ❌ Depreciado | ✅ Substituto | Motivo |
|---------------|---------------|--------|
| `@Autowired` em campo | Constructor injection | Testabilidade, imutabilidade |
| `RestTemplate` | `RestClient` | Modern API, Virtual Threads support |
| `WebClient` para blocking I/O | `RestClient` | WebClient é reativo; RestClient é blocking |
| `RestTemplateBuilder` | `RestClient.Builder` | Consistência com nova API |
| `@RequestMapping` | `@GetMapping`, `@PostMapping`, etc. | Mais explícito, legível |
| `PathMatchConfigurer` antiga | Nova `PathMatchConfigurer` | Spring Boot 4 config API |


## Java SE (Java 25)

| ❌ Depreciado | ✅ Substituto | Motivo |
|---------------|---------------|--------|
| `ThreadLocal` | `ScopedValue` | Compatível com Virtual Threads |
| `synchronized` blocks/methods | `ReentrantLock` | Evita pinning em Virtual Threads |
| `Thread` direto | `VirtualThread` via executor | Escalabilidade I/O-bound |
| `Date`, `Calendar` | `Instant`, `LocalDate` (java.time) | API moderna, imutável |
| `SimpleDateFormat` | `DateTimeFormatter` | Thread-safe, moderno |
| `finalize()` | `try-with-resources`, `Cleaner` | Previsível, não-blocking |
| `SecurityManager` | Proteção via OS/container | Removido em Java 24+ |


## Jakarta EE / Bibliotecas

| ❌ Depreciado | ✅ Substituto | Motivo |
|---------------|---------------|--------|
| `javax.*` namespace | `jakarta.*` namespace | Jakarta EE 11 |
| Apache HttpClient 4.x | Apache HttpClient 5.x | Moderno, reactive streams |
| Jackson default typing | Configuração segura explicita | Segurança |
| Joda-Time | `java.time` | Padrão Java desde Java 8 |
| Lombok `@Data` em entities | Records, ou getters/setters | JPA compatibility |


## Banco de Dados / JPA

| ❌ Depreciado | ✅ Substituto | Motivo |
|---------------|---------------|--------|
| Hibernate 5.x | Hibernate 7.x | Suporte a Jakarta EE 11 |
| `EntityManager.merge()` para inserts | `persist()` | Semântica correta |
| `CascadeType.ALL` indiscriminado | Cascades específicos | Controle de operações |
| `FetchType.EAGER` | `FetchType.LAZY` com explicit joins | Performance |


## Build / Ferramentas

| ❌ Depreciado | ✅ Substituto | Motivo |
|---------------|---------------|--------|
| Maven 3.8.x ou anterior | Maven 3.9.x ou 4.x | Suporte a Java 25 |
| Gradle 7.x | Gradle 8.5+ | Suporte a Java 25 |
| `maven.compiler.source/target` | `maven.compiler.release` | Semântica correta |


## Checklist de Modernização

- [ ] Substituir `@Autowired` por constructor injection
- [ ] Migrar `RestTemplate` para `RestClient`
- [ ] Usar anotações específicas (`@GetMapping`) em vez de `@RequestMapping`
- [ ] Habilitar Virtual Threads: `spring.threads.virtual.enabled=true`
- [ ] Substituir `synchronized` por `ReentrantLock`
- [ ] Substituir `ThreadLocal` por `ScopedValue`
- [ ] Migrar `Date/Calendar` para `java.time`
- [ ] Atualizar imports `javax.*` para `jakarta.*`
- [ ] Verificar versões Maven/Gradle compatíveis
- [ ] Revisar warnings de deprecação no build
