// Examples: Rich Domain Model vs Anemic Domain Model
// Source: best-practices/SKILL.md

package com.example.app;

import java.time.Instant;
import java.util.UUID;

// ========== ANEMIC DOMAIN MODEL (NÃO FAZER) ==========

// ❌ Anemic Domain Model — apenas getters/setters
class AnemicOrder {
    private UUID id;
    private double total;
    private String status;
    // apenas getters/setters...
}

class AnemicOrderService {
    public void confirmOrder(AnemicOrder order) {
        if (!"PENDING".equals(order.getStatus())) {
            throw new IllegalStateException("Only pending orders can be confirmed");
        }
        order.setStatus("CONFIRMED");
        order.setConfirmedAt(Instant.now());
    }
}

// ========== RICH DOMAIN MODEL (FAZER) ==========

// ✅ Rich Domain Model — comportamento encapsulado
class RichOrder {
    private final UUID id;
    private Money total;
    private OrderStatus status;
    private Instant confirmedAt;

    public void confirm() {
        if (!canBeConfirmed()) {
            throw new OrderCannotBeConfirmedException(id, status);
        }
        this.status = OrderStatus.CONFIRMED;
        this.confirmedAt = Instant.now();
        registerEvent(new OrderConfirmedEvent(this.id));
    }

    public boolean canBeConfirmed() {
        return status == OrderStatus.PENDING && total.isPositive();
    }

    public void addItem(Product product, int quantity) {
        validateCanBeModified();
        // lógica de adicionar item
    }

    private void validateCanBeModified() {
        if (status != OrderStatus.PENDING) {
            throw new OrderCannotBeModifiedException(id, status);
        }
    }

    // Getters
    public UUID getId() { return id; }
    public OrderStatus getStatus() { return status; }
    public Instant getConfirmedAt() { return confirmedAt; }
    void setStatus(String s) {}
    void setConfirmedAt(Instant i) {}
    String getStatusString() { return status.name(); }
}

// Supporting classes
enum OrderStatus { PENDING, CONFIRMED, SHIPPED, DELIVERED }
class Money {
    boolean isPositive() { return true; }
}
class Product {}
class OrderCannotBeConfirmedException extends RuntimeException {
    OrderCannotBeConfirmedException(UUID id, OrderStatus status) { super("Order " + id + " cannot be confirmed"); }
}
class OrderCannotBeModifiedException extends RuntimeException {
    OrderCannotBeModifiedException(UUID id, OrderStatus status) { super("Order " + id + " cannot be modified"); }
}
class OrderConfirmedEvent {
    OrderConfirmedEvent(UUID id) {}
}
interface DomainEvent {}
