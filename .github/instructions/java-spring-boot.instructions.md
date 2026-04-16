---
name: java-spring-boot
description: "Use when working on Java 25 and Spring Boot 4 code, especially for hexagonal architecture, Spring conventions, DTO design, validation, logging, and testing expectations."
applyTo: "**/*.java"
---

# Java 25 + Spring Boot 4 Instructions

## Language & Framework

- Java 25 with preview features enabled
- Spring Boot 4.x with Spring Framework 7.x
- Jakarta EE 11 APIs (jakarta.* namespace)

## Code Style

- Follow Google Java Style Guide
- Use records for all DTOs, value objects, and configuration properties
- Use sealed interfaces/classes for closed type hierarchies
- Use pattern matching with switch expressions
- Use Virtual Threads for I/O-bound operations
- Use Structured Concurrency (`StructuredTaskScope`) for parallel tasks
- Prefer `var` for local variables when type is obvious

## Spring Conventions

- Constructor injection only — never use `@Autowired` on fields
- Prefer component scanning for application services and adapters instead of manual `@Bean` registration
- Use `@Component` for application-layer use cases when they need to be Spring-managed
- Use `@Service` only when the type is an explicit domain/application service rather than a single use case
- Use `@Repository` only for persistence adapters and repository implementations
- Keep `@Bean` methods restricted to infrastructure/transversal objects such as `Clock`, `PasswordEncoder`, HTTP clients, and external SDK/configuration factories
- `@ConfigurationProperties` bound to record classes
- Error handling via `ProblemDetail` (RFC 9457)
- HTTP clients via `RestClient` or `@HttpExchange` interfaces
- Observability via Micrometer + OpenTelemetry
- Virtual Threads enabled: `spring.threads.virtual.enabled=true`

## Architecture

- Hexagonal / Ports & Adapters architecture
- Domain layer: entities, value objects, repository interfaces, domain services — no framework dependencies
- Application layer: use cases, ports, DTOs
- Application use cases may use Spring stereotypes for DI, but should remain free of web/persistence concerns
- Infrastructure layer: persistence, HTTP clients, messaging, configuration
- Infrastructure configuration should not become a registry of application use cases already discoverable by component scan
- Adapter layer: REST controllers, JPA implementations, message listeners
- Split REST controllers by HTTP resource, subresource, or workflow responsibility when mixed concerns start to accumulate
- Do not split controllers one-per-use-case unless that boundary is also visible in the HTTP API

## Testing

- JUnit 6 + Mockito 5 + AssertJ for unit tests
- Use `MockMvc` for servlet controller tests
- In MockMvc controller tests, include the global `@RestControllerAdvice` explicitly in the setup so `ProblemDetail` and error mapping are exercised
- Testcontainers + WireMock for integration tests
- ArchUnit for architecture validation
- JaCoCo coverage ≥ 80%
- Test naming: `should_[result]_when_[condition]`

## Null Safety

- Use `Optional<T>` for return types that may be absent
- Never use `Optional` as parameter or field type
- Use `@Nullable` / `@NonNull` annotations from Spring framework
- Validate inputs at entry points with Bean Validation

## Logging

- SLF4J with Logback
- Structured logging with MDC context
- Never use `System.out` or `System.err`
- Log levels: ERROR for failures, WARN for degraded, INFO for business events, DEBUG for troubleshooting
