# Race Conditions — Guia Técnico

Condição de corrida ocorre quando múltiplas threads acessam dados compartilhados simultaneamente sem sincronização adequada, com pelo menos uma operação de escrita.

## Cenários e Soluções

| Cenário | Problema | Solução |
|---------|----------|---------|
| Contador compartilhado | `count++` não é atômico | `AtomicInteger`, `LongAdder` |
| Check-then-act | verificação e ação separadas | `computeIfAbsent`, `putIfAbsent` |
| Seção crítica com Virtual Threads | `synchronized` causa pinning | `ReentrantLock` com try-finally |
| Leitura predominante | `ReentrantLock` muito restritivo | `StampedLock` com leitura otimista |
| Estado compartilhado | mutabilidade em si é o problema | `record` imutável + `List.copyOf()` |
| Coleção compartilhada | `ArrayList` não é thread-safe | `ConcurrentHashMap`, `CopyOnWriteArrayList` |

## Regras de Ouro

- **Imutabilidade primeiro**: se o estado não muda, não há race condition.
- **`synchronized` evitado**: causa pinning de Virtual Threads na carrier thread.
- **`ReentrantLock` sempre** com `try { ... } finally { lock.unlock(); }`.
- **`StampedLock`** para cargas de leitura intensiva (modo otimista evita lock).
- **`AtomicXxx`** para contadores e flags simples.
- **`ConcurrentHashMap.computeIfAbsent`** em vez de check-then-put.

## Detecção

| Sintoma | Causa Provável |
|---------|---------------|
| Resultado inconsistente com `++`/`--` | Contador não atômico |
| `ConcurrentModificationException` | Iteração e modificação simultâneas |
| Cache retornando dados obsoletos | Visibilidade de memória sem `volatile`/lock |
| Deadlock — threads bloqueadas mutuamente | Ordem de aquisição de locks inconsistente |

## Exemplo

Ver `examples/RaceConditionsExamples.java` — `AtomicInteger`, `LongAdder`, `computeIfAbsent`, `ReentrantLock`, `StampedLock`, imutabilidade.
