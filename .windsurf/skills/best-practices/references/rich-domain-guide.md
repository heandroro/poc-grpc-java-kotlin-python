# Rich Domain Model — Guia Técnico

Guia para modelagem de domínio rico vs modelo anêmico em Java 25.

## Modelo Anêmico vs Rico

| Aspecto | Anêmico (Anti-pattern) | Rico (Recomendado) |
|---------|----------------------|--------------------|
| Local da lógica | Toda nos Services | Nas Entities/Value Objects |
| Validação | Services | Construtores e métodos de domínio |
| Estado | Getters/setters públicos | Acesso controlado via métodos |
| Reutilização | Duplicação entre Services | Encapsulada no domínio |
| Testabilidade | Mocks complexos nos Services | Entidades testáveis isoladamente |
| Expressividade | Procedural | Linguagem ubíqua |

**Regra**: se `OrderService` precisa ler e modificar o estado interno de `Order`, o comportamento pertence à `Order`.

## Componentes do Domain Model

| Componente | Tipo Java | Identidade | Mutável | Responsabilidade |
|------------|-----------|-----------|---------|------------------|
| Value Object | `record` | Por valor (campos) | ❌ | Representar conceitos imutáveis (`Money`, `Email`) |
| Entity | `class` | Por `id` único | ✅ | Estado e comportamento com identidade |
| Aggregate Root | `class` (@Entity) | Por `id` | ✅ | Ponto de entrada único; garante invariantes do agregado |
| Domain Event | `record` | Por `eventId` | ❌ | Registrar fatos ocorridos no domínio |

### Value Objects — regras
- Implementados como `record` com validação no compact constructor.
- Operações retornam novo VO, nunca mutam o existente: `money.add(other)` → novo `Money`.
- Primitivos do domínio: `Money`, `Email`, `OrderId`, `DateRange`, `CPF`.

### Entities e Aggregates — regras
- Construtor `protected` com no-arg para JPA; factory method `static create(...)` público.
- Acesso por leitura via métodos sem `set` público — estado muda apenas por comportamento.
- Domain Events publicados com `registerEvent()` e liberados pelo `Service` após `save()`.
- Regras de invariante no método, não no Service: `order.confirm()` valida o estado internamente.

## Checklist Rich Domain

- [ ] Entities têm comportamento, não apenas getters/setters
- [ ] Value Objects são imutáveis e validam estado no construtor
- [ ] Aggregate Root controla acesso às entidades filhas
- [ ] Domain Events notificam mudanças importantes de estado
- [ ] Regras de negócio estão no domínio, não em Services
- [ ] Services orquestram, não implementam regras de negócio
- [ ] Testes de domínio não dependem de infraestrutura

## Exemplos

Ver `examples/RichDomainModel.java` — Anemic vs Rich Order, Value Objects (`Money`, `Email`), Aggregate Root, Domain Events.
