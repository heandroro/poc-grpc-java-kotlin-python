# Quality Rules

Checklist detalhado para code review e quality gate em projetos Java 25 + Spring Boot 4.

## Quality & Style

- [ ] Follows Google Java Style / project Checkstyle rules
- [ ] No SpotBugs, PMD, or Error Prone warnings
- [ ] Cyclomatic complexity ≤ 10 per method
- [ ] Methods ≤ 30 lines, classes ≤ 300 lines
- [ ] No dead code, unused imports, or TODOs without tracking
- [ ] `@SuppressWarnings` is used only with explicit justification

## Java 25 Idioms

- [ ] Uses records for DTOs and value objects
- [ ] Uses sealed interfaces for closed domain types
- [ ] Uses pattern matching in switch expressions
- [ ] Uses Virtual Threads where appropriate (I/O-bound)
- [ ] Uses Structured Concurrency for parallel operations
- [ ] Uses unnamed variables (`_`) for unused bindings
- [ ] Uses Scoped Values instead of `ThreadLocal` with Virtual Threads when relevant

## Spring Boot 4 Patterns

- [ ] Constructor injection only (no field `@Autowired`)
- [ ] `@ConfigurationProperties` with record classes
- [ ] `ProblemDetail` (RFC 9457) for error responses
- [ ] `RestClient` or `@HttpExchange` for HTTP calls
- [ ] Bean Validation on input DTOs
- [ ] Observability configured with Micrometer and OpenTelemetry when applicable

## Architecture & SOLID

- [ ] Domain layer has no Spring or infrastructure imports
- [ ] Controllers only in `adapter.web`
- [ ] Repository interfaces in domain, implementations in infrastructure
- [ ] No circular dependencies
- [ ] ArchUnit tests cover new architectural rules where relevant
- [ ] Single Responsibility is preserved
- [ ] Open/Closed is preserved
- [ ] Dependency Inversion is preserved

## Clean Code & Domain Modeling

- [ ] Names reveal intent and avoid vague verbs like `process()` or `handle()`
- [ ] Methods are small and do one thing
- [ ] Comments are necessary and not compensating for unclear code
- [ ] No boolean flag arguments unless justified
- [ ] Domain entities contain behavior, not only data accessors
- [ ] Value objects model important domain concepts
- [ ] Business invariants are enforced in the domain layer

## Mapping, Exceptions, And Null Safety

- [ ] MapStruct is used where layer mapping is non-trivial
- [ ] Exception hierarchy is coherent and preserves original causes
- [ ] `@ControllerAdvice` or equivalent maps exceptions to `ProblemDetail`
- [ ] No exceptions used for normal control flow
- [ ] Uses `Optional<T>` only for return values, not parameters or fields
- [ ] Uses empty collections instead of `null`
- [ ] Null-safety annotations and validation are consistent
- [ ] Does not return `null` when `Optional` or empty collections are expected

## Testing

- [ ] Unit tests exist for all new or modified logic
- [ ] Tests follow `should_X_when_Y` naming
- [ ] Uses BDDMockito (`given/when/then`) where Mockito is used
- [ ] Integration tests use Testcontainers with fixed image tags
- [ ] Coverage is at least 80% for modified files
- [ ] Tests are deterministic and independent of execution order
- [ ] Architecture tests are updated when package structure or boundaries change

## Security

- [ ] No hardcoded secrets or credentials
- [ ] Input validation on all external data
- [ ] Proper authentication and authorization checks
- [ ] SQL injection and XSS prevention
- [ ] Dependencies do not introduce obvious known vulnerabilities
- [ ] User-facing messages do not leak technical internals

## Performance, Concurrency, And Deployability

- [ ] No N+1 query problems
- [ ] Appropriate caching for hot paths
- [ ] Connection pool sizing reviewed
- [ ] No blocking calls on Virtual Threads hot paths
- [ ] Shared mutable state is synchronized or replaced with safe alternatives
- [ ] No unsafe check-then-act race conditions
- [ ] Dockerfile uses multi-stage build and non-root user when applicable
- [ ] Health probes and container JVM settings are appropriate when applicable

## Review Outcome

- [ ] State whether the change is ready to merge
- [ ] Highlight missing tests, missing observability, or operational risks even if no code bug is found