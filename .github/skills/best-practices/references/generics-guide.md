# Generics — Guia Técnico

Generics garantem segurança de tipos em tempo de compilação, eliminando casts e tornando APIs mais expressivas e reutilizáveis.

## Quando Usar

| Situação | Padrão |
|----------|--------|
| Repositório com operações CRUD reutilizáveis | `Repository<T, ID>` |
| Operação que pode falhar com erro tipado | `Result<T>` (sealed) |
| Lista paginada reutilizável | `Page<T>` com `map()` |
| Envelope de resposta da API | `ApiResponse<T>` |
| Par de valores heterogêneos | `Pair<A, B>` |
| Cache com expiração | `Cache<K, V>` |
| Handlers de comandos (CQRS) | `CommandHandler<C, R>` |

## Bounded Types — Regras

| Sintaxe | Uso | Mnemônico |
|---------|-----|-----------|
| `<T extends Foo>` | Receber qualquer subtype de Foo | Upper bound |
| `List<? extends T>` | Ler da coleção (producer) | **P**roducer **E**xtends |
| `List<? super T>` | Escrever na coleção (consumer) | **C**onsumer **S**uper |
| `List<?>` | Apenas ler, sem conhecer o tipo | Wildcard unbounded |

**Regra PECS**: *Producer Extends, Consumer Super*.

## Anti-patterns

| ❌ Evitar | ✅ Correto |
|-----------|-----------|
| `List list = new ArrayList()` | `List<Order> list = new ArrayList<>()` |
| `public List<?> findOrders()` | `public List<Order> findOrders()` |
| `instanceof List<String>` | verificar o elemento, não o container |
| Wildcard em tipo de retorno | Tipo concreto no retorno |

## Exemplos

Ver `examples/GenericsExamples.java` — `Result<T>`, `Page<T>`, `Cache<K,V>`, `Repository<T,ID>`, PECS, CQRS.
