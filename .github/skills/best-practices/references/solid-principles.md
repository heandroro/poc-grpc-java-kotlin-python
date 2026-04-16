# SOLID Principles - Resumo

## S — Single Responsibility Principle (SRP)

**Uma classe deve ter um, e apenas um, motivo para mudar.**

```java
// ❌ Ruim: múltiplas responsabilidades
class OrderService {
    void createOrder() { }
    void sendEmail() { }      // viola SRP
    void generateInvoice() { } // viola SRP
}

// ✅ Bom: cada classe com responsabilidade única
class OrderService { void create() { } }
class NotificationService { void sendEmail() { } }
class InvoiceService { void generate() { } }
```

## O — Open/Closed Principle (OCP)

**Aberto para extensão, fechado para modificação.**

```java
// ❌ Ruim: modifica para cada novo tipo
class DiscountCalculator {
    double calculate(String type, double amount) {
        if ("PREMIUM".equals(type)) return amount * 0.9;
        if ("VIP".equals(type)) return amount * 0.8;
        return amount;
    }
}

// ✅ Bom: extensão via novas classes
interface DiscountPolicy {
    double apply(double amount);
}

class PremiumDiscount implements DiscountPolicy {
    public double apply(double amount) { return amount * 0.9; }
}
```

## L — Liskov Substitution Principle (LSP)

**Subtipos devem ser substituíveis por seus tipos base.**

```java
// ❌ Ruim: quebra contrato da classe base
class Rectangle {
    void setWidth(double w) { this.width = w; }
    void setHeight(double h) { this.height = h; }
}

class Square extends Rectangle {  // viola LSP
    void setWidth(double w) { width = height = w; }  // surpreendente
}

// ✅ Bom: usar composição ao invés de herança
interface Shape {
    double area();
}

class Rectangle implements Shape { ... }
class Square implements Shape { ... }
```

## I — Interface Segregation Principle (ISP)

**Clientes não devem ser forçados a depender de métodos que não usam.**

```java
// ❌ Ruim: interface gorda
interface Worker {
    void work();
    void eat();
    void sleep();
}

// ✅ Bom: interfaces coesas
interface Workable { void work(); }
interface Feedable { void eat(); }
interface Sleepable { void sleep(); }

class Human implements Workable, Feedable, Sleepable { ... }
class Robot implements Workable { ... }  // não precisa eat/sleep
```

## D — Dependency Inversion Principle (DIP)

**Dependa de abstrações, não de implementações.**

```java
// ❌ Ruim: depende de implementação concreta
class OrderService {
    private final JpaOrderRepository repository;  // concreto
}

// ✅ Bom: depende de abstração
interface OrderRepository {
    Order save(Order order);
}

class OrderService {
    private final OrderRepository repository;  // abstração
}
```

## Checklist de Aplicação

- [ ] Cada classe tem uma responsabilidade clara
- [ ] Novos comportamentos são adicionados via extensão, não modificação
- [ ] Herança respeita contratos da classe base
- [ ] Interfaces são pequenas e focadas
- [ ] Injeção de dependências usa interfaces, não classes concretas
