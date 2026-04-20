# Streams — Guia Técnico

Guia completo de uso da Stream API em Java 25.

## Quando Usar

| Cenário | Usar Stream? | Alternativa |
|---------|--------------|-------------|
| Transformação de coleções (filter/map) | ✅ Sim | — |
| Agregações (sum, avg, group) | ✅ Sim | — |
| Flattening de coleções aninhadas | ✅ Sim | — |
| Busca única (findFirst/findAny) | ✅ Sim | — |
| Operações simples (contains, indexOf) | ❌ Não | Método da coleção |
| Acesso por índice | ❌ Não | `for` tradicional |
| Modificação de estado externo | ❌ Não | `forEach` com cuidado |

## Operações Intermediárias

| Operação | Descrição | Exemplo |
|----------|-----------|---------|
| `filter(Predicate)` | Filtra elementos | `.filter(User::isActive)` |
| `map(Function)` | Transforma cada elemento | `.map(User::email)` |
| `flatMap(Function)` | Flatten coleções aninhadas | `.flatMap(u -> u.roles().stream())` |
| `distinct()` | Remove duplicados | `.distinct()` |
| `sorted()` / `sorted(Comparator)` | Ordena | `.sorted(Comparator.comparing(User::name))` |
| `peek(Consumer)` | Inspeção (debug) | `.peek(log::info)` |
| `limit(n)` | Limita resultados | `.limit(10)` |
| `skip(n)` | Pula elementos | `.skip(20)` |
| `takeWhile(Predicate)` | Java 9+ — pega enquanto condição | `.takeWhile(x -> x < 100)` |
| `dropWhile(Predicate)` | Java 9+ — descarta enquanto condição | `.dropWhile(String::isBlank)` |

## Operações Terminais

| Operação | Retorno | Uso |
|----------|---------|-----|
| `toList()` | `List<T>` | Preferido (Java 16+) |
| `collect(Collectors.toList())` | `List<T>` | Legado |
| `collect(Collectors.toSet())` | `Set<T>` | Remove duplicados |
| `collect(Collectors.groupingBy())` | `Map<K, List<T>>` | Agrupamento |
| `collect(Collectors.joining())` | `String` | Concatenação |
| `findFirst()` | `Optional<T>` | Primeiro elemento |
| `findAny()` | `Optional<T>` | Qualquer elemento (parallel) |
| `anyMatch(Predicate)` | `boolean` | Se algum satisfaz |
| `allMatch(Predicate)` | `boolean` | Se todos satisfazem |
| `noneMatch(Predicate)` | `boolean` | Se nenhum satisfaz |
| `count()` | `long` | Contagem |
| `reduce(identity, BinaryOperator)` | `T` | Agregação |
| `forEach(Consumer)` | `void` | Ação final (evitar side effects) |

## Boas Práticas

### ✅ Fazer

```java
// Method references para lambdas simples
list.stream().map(String::toUpperCase);

// toList() em vez de collect() (Java 16+)
var emails = users.stream()
    .filter(User::isActive)
    .map(User::email)
    .toList();

// Optional com tratamento explícito
var first = list.stream()
    .findFirst()
    .orElseThrow(() -> new NotFoundException("Nenhum item"));

// flatMap para coleções aninhadas
var allPermissions = users.stream()
    .flatMap(u -> u.permissions().stream())
    .distinct()
    .toList();
```

### ❌ Não Fazer

```java
// Stream para operações simples
list.stream().anyMatch(x -> x.equals(target));  // ruim
list.contains(target);  // bom

// forEach com side effects
list.stream().forEach(item -> counter++);  // ruim

// findFirst().get() sem verificação
list.stream().findFirst().get();  // ruim — pode lançar exceção

// Lambdas complexas inline
list.stream().filter(u -> {
    if (u.isActive()) {
        if (u.isPremium()) {
            return true;
        }
    }
    return false;
});  // ruim — extraia método
```

## Parallel Streams

```java
// Apenas para coleções grandes (>10k) e operações CPU-bound
var result = largeList.parallelStream()
    .map(this::expensiveOperation)
    .toList();

// NUNCA use parallelStream para:
// - Operações I/O (use Virtual Threads)
// - Coleções pequenas (overhead > ganho)
// - Operações com synchronized
```

## Optional com Streams

```java
// Encadeamento de Optional
Optional<Order> findOrder(String id) {
    return Optional.ofNullable(id)
        .filter(this::isValidUuid)
        .flatMap(repository::findById);
}

// Stream de Optionals — filtrar presentes
List<Order> orders = orderIds.stream()
    .map(repository::findById)
    .flatMap(Optional::stream)  // Java 9+
    .toList();
```

## Padrões Comuns

```java
// Grouping com downstream collector
Map<Status, Long> countByStatus = orders.stream()
    .collect(groupingBy(Order::status, counting()));

// Partitioning (boolean predicate)
Map<Boolean, List<User>> partitioned = users.stream()
    .collect(partitioningBy(User::isActive));

// Max/Min com Comparator
Optional<User> oldest = users.stream()
    .max(Comparator.comparing(User::birthDate));

// Redução customizada
Money total = orders.stream()
    .map(Order::amount)
    .reduce(Money.ZERO, Money::add);
```
