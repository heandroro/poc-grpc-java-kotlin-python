import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

// ============================================================
// USA RECORD: DTOs de entrada e saída
// ============================================================

record CreateOrderRequest(String customerId, List<String> items) {}

record OrderResponse(UUID id, String customerId, List<String> items, BigDecimal total, String status) {}

// ============================================================
// USA RECORD: Value Objects imutáveis com validação
// ============================================================

record Money(BigDecimal amount, String currency) {
    Money {
        Objects.requireNonNull(amount, "amount is required");
        Objects.requireNonNull(currency, "currency is required");
        if (amount.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Amount cannot be negative");
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    Money add(Money other) {
        if (!this.currency.equals(other.currency)) throw new IllegalArgumentException("Currency mismatch");
        return new Money(this.amount.add(other.amount), this.currency);
    }
}

record Email(String value) {
    private static final Pattern PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    Email {
        Objects.requireNonNull(value, "email is required");
        if (!PATTERN.matcher(value).matches()) throw new IllegalArgumentException("Invalid email: " + value);
    }
}

// ============================================================
// USA RECORD: Range com comportamento puro
// ============================================================

record DateRange(LocalDate start, LocalDate end) {
    DateRange {
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);
        if (end.isBefore(start)) throw new IllegalArgumentException("End must be after start");
    }

    boolean contains(LocalDate date) { return !date.isBefore(start) && !date.isAfter(end); }
    long daysLength()                { return ChronoUnit.DAYS.between(start, end); }
    DateRange extendTo(LocalDate newEnd) { return new DateRange(start, newEnd); }
}

// ============================================================
// USA RECORD: Eventos de domínio (fatos imutáveis)
// ============================================================

record OrderConfirmedEvent(UUID orderId, String customerId, Money total) {}

// ============================================================
// USA CLASS: Aggregate Root com estado mutável
// ============================================================

class Order {
    private final UUID id;
    private final String customerId;
    private final List<String> items;
    private OrderStatus status;

    private Order(UUID id, String customerId, List<String> items) {
        this.id         = Objects.requireNonNull(id);
        this.customerId = Objects.requireNonNull(customerId);
        this.items      = new ArrayList<>(items);
        this.status     = OrderStatus.PENDING;
    }

    static Order create(String customerId, List<String> items) {
        return new Order(UUID.randomUUID(), customerId, items);
    }

    // Comportamento no agregado (Rich Domain)
    void confirm() {
        if (status != OrderStatus.PENDING) throw new IllegalStateException("Cannot confirm order in state: " + status);
        this.status = OrderStatus.CONFIRMED;
    }

    void cancel(String reason) {
        if (status == OrderStatus.SHIPPED) throw new IllegalStateException("Cannot cancel shipped order");
        this.status = OrderStatus.CANCELLED;
    }

    UUID id()           { return id; }
    OrderStatus status(){ return status; }

    enum OrderStatus { PENDING, CONFIRMED, SHIPPED, CANCELLED }
}

// ============================================================
// USA CLASS: JPA Entity (requer mutabilidade e no-arg constructor)
// ============================================================

// @Entity — anotações omitidas propositalmente (não-compilável sem JPA no classpath)
class OrderEntity {
    private UUID id;
    private String customerId;
    private String status;

    protected OrderEntity() {} // required by JPA

    static OrderEntity from(Order order) {
        var entity = new OrderEntity();
        entity.id         = order.id();
        entity.customerId = "customer";
        entity.status     = order.status().name();
        return entity;
    }
}

// ============================================================
// USA CLASS: Builder para construção complexa
// ============================================================

class Report {
    private final String title;
    private final List<String> sections;

    private Report(Builder b) {
        this.title    = b.title;
        this.sections = List.copyOf(b.sections);
    }

    static Builder builder(String title) { return new Builder(title); }

    static class Builder {
        private final String title;
        private final List<String> sections = new ArrayList<>();

        private Builder(String title) {
            this.title = Objects.requireNonNull(title);
        }

        Builder addSection(String section) { sections.add(section); return this; }

        Report build() {
            if (sections.isEmpty()) throw new IllegalStateException("Report needs at least one section");
            return new Report(this);
        }
    }
}
