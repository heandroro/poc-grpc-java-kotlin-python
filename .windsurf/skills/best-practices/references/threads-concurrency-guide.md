# Threads e Concorrência — Guia Técnico

Guia de concorrência moderna em Java 25 com Virtual Threads e Structured Concurrency.

## Virtual Threads (Project Loom)

Virtual Threads são gerenciadas pela JVM, não pelo OS. Permitem milhões de threads simultâneas para workloads I/O-bound.

| | Platform Thread | Virtual Thread |
|--|----------------|----------------|
| **Custo de criação** | Alto (~1 MB stack) | Baixo (~KB) |
| **Bloqueio I/O** | Bloqueia a OS thread | Suspende; libera a carrier thread |
| **Uso recomendado** | CPU-bound intensivo | I/O-bound (HTTP, DB, filesystem) |
| **Pool** | `ThreadPoolExecutor` | `newVirtualThreadPerTaskExecutor()` |

**Ativar no Spring Boot:**
```yaml
spring.threads.virtual.enabled: true
```

## Ferramentas de Sincronização

| Necessidade | Ferramenta | Evitar |
|-------------|-----------|--------|
| Seção crítica | `ReentrantLock` | `synchronized` (pinning) |
| Leitura predominante | `StampedLock` (optimistic) | `ReadWriteLock` |
| Variável de contexto | `ScopedValue` | `ThreadLocal` |
| Contador simples | `AtomicLong`, `LongAdder` | campo `long` não sincronizado |
| Coleção compartilhada | `ConcurrentHashMap`, `CopyOnWriteArrayList` | `HashMap`, `ArrayList` |
| Flag booleana | `AtomicBoolean` | `boolean` sem `volatile` |

**Por que evitar `synchronized` com Virtual Threads?** Ele faz *pinning* — fixa a Virtual Thread na carrier thread durante o lock, eliminando o benefício de escalabilidade.

## Structured Concurrency

Garante que tarefas filhas não sobrevivam ao escopo que as criou. Substitui `CompletableFuture` encadeado para operações paralelas:

```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    var userTask  = scope.fork(() -> userService.findById(userId));
    var orderTask = scope.fork(() -> orderService.findByUser(userId));
    scope.join().throwIfFailed();
    return new Dashboard(userTask.get(), orderTask.get());
}
```

## ScopedValue vs ThreadLocal

| | `ThreadLocal` | `ScopedValue` |
|--|--------------|---------------|
| **Ciclo de vida** | Manual (`remove()` necessário) | Léxico — escopo delimitado |
| **Virtual Threads** | ❌ Vazamentos em pools | ✅ Projetado para VT |
| **Mutabilidade** | Mutável | Imutável dentro do escopo |
| **Uso** | `set()` / `get()` | `ScopedValue.runWhere(KEY, val, task)` |

## Checklist

- [ ] `spring.threads.virtual.enabled=true` no `application.yml`
- [ ] I/O-bound usa Virtual Threads; CPU-bound usa thread pool dedicado
- [ ] Nenhum `synchronized` em código novo — usar `ReentrantLock`
- [ ] `ThreadLocal` substituído por `ScopedValue`
- [ ] `StructuredTaskScope` para chamadas paralelas com cancelamento coordenado
- [ ] `AtomicXxx` para contadores e flags compartilhados
- [ ] Coleções compartilhadas são `Concurrent*` ou imutáveis

## Exemplos

Ver `examples/VirtualThreads.java` — criação de VT, `StructuredTaskScope`, `ReentrantLock`, `ScopedValue`, `StampedLock`.  
Ver `examples/RaceConditionsExamples.java` — padrões de sincronização e coleções thread-safe.
