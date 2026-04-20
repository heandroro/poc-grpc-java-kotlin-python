# Record vs Class — Guia de Decisão

## Regra Central

| Critério | `record` | `class` |
|----------|----------|---------|
| **Mutabilidade** | Imutável por natureza | Estado muda ao longo do ciclo de vida |
| **Identidade** | Por valor (`equals` via campos) | Por referência (`id` único) |
| **JPA/Hibernate** | ❌ Não compatível | ✅ Obrigatório |
| **Herança** | ❌ Não suporta `extends` | ✅ Suporta |
| **Construção** | Todos os campos no construtor | Incremental via Builder ou factory |

## Tabela de Decisão por Tipo

| Tipo | `record` | `class` |
|------|----------|---------|
| DTO Request/Response | ✅ | — |
| Value Object (`Money`, `Email`) | ✅ | — |
| Domain Event | ✅ | — |
| `@ConfigurationProperties` | ✅ | — |
| Projeção de leitura (Spring Data) | ✅ | — |
| JPA Entity | ❌ | ✅ |
| Aggregate Root | ❌ | ✅ |
| Exception | ❌ | ✅ |
| Service / Component | ❌ | ✅ |
| Builder complexo | ❌ | ✅ |

## Records Podem Ter Comportamento

Records são imutáveis, mas não são passivos. Podem conter:
- Validação no compact constructor
- Métodos de negócio que retornam novos records (sem mutar)
- Constantes como `DiscountPolicy.NO_DISCOUNT`

```java
record DateRange(LocalDate start, LocalDate end) {
    DateRange { if (end.isBefore(start)) throw new IllegalArgumentException(); }

    boolean contains(LocalDate date) { return !date.isBefore(start) && !date.isAfter(end); }
    DateRange extendTo(LocalDate newEnd) { return new DateRange(start, newEnd); }
}
```

## Records NÃO substituem JPA Entities

JPA exige: construtor sem argumentos, campos mutáveis e equals/hashCode por `id`. Records quebram as três regras.

Use records como **projeções** de leitura ao lado da entity:

```java
@Entity class Order { ... }  // escrita

// Spring Data Projection — record para leitura
record OrderSummary(UUID id, String status, BigDecimal total) {}
List<OrderSummary> summaries = repo.findAllProjectedBy();
```

## Exemplos

Ver `examples/RecordVsClass.java` — DTOs, Value Objects, Aggregate Root, JPA Entity, Builder.
