# Exceções — Guia Técnico

Guia para hierarquia de exceções de domínio e tratamento de erros padronizado com Problem Detail (RFC 9457).

## Hierarquia Recomendada

```
RuntimeException
  └── DomainException (base abstrata com errorCode + httpStatus)
        ├── NotFoundException         (404)
        ├── ConflictException         (409)
        ├── ValidationException       (400)
        └── ForbiddenException        (403)
```

Cada exceção de domínio deve incluir:
- **`errorCode`** — identificador único e documentável (ex: `ORDER_001`)
- **`httpStatus`** — status HTTP correspondente
- **Contexto rico** — IDs, valores relevantes no construtor

## Tratamento Global — Problem Detail (RFC 9457)

Spring Boot 4 suporta `ProblemDetail` nativamente. Configure um `@RestControllerAdvice` com:

| Handler | Exceção | Status |
|---------|---------|--------|
| `handleDomain` | `DomainException` | definido na exception |
| `handleValidation` | `MethodArgumentNotValidException` | 400 |
| `handleNotFound` | `NoResourceFoundException` | 404 |
| `handleGeneric` | `Exception` | 500 |

## Boas Práticas

| ✅ Fazer | ❌ Não Fazer |
|---------|-------------|
| Exceções específicas de domínio | `throw new RuntimeException("error")` |
| Contexto rico no construtor | Mensagem genérica sem dados |
| `ProblemDetail` em todas as APIs | JSON de erro ad-hoc |
| Preservar `cause` ao encadear | `new Exception(e.getMessage())` |
| Logar WARN para negócio, ERROR para falha técnica | Logar e relançar (duplica logs) |
| `throws` declarado só para checked exceptions | Swallow silencioso com catch vazio |

## Checklist

- [ ] `DomainException` base implementada com `errorCode` e `httpStatus`
- [ ] `@RestControllerAdvice` retorna `ProblemDetail` para todas as rotas
- [ ] Error codes únicos, com prefixo de bounded context (`ORDER_001`, `STOCK_002`)
- [ ] Exceções contêm dados contextuais (IDs, valores)
- [ ] Nenhum `catch` silencioso sem log ou relançamento

## Exemplos

Ver `examples/ExceptionsExamples.java` — hierarquia completa, `OrderNotFoundException`, `InsufficientStockException`, anti-patterns.
