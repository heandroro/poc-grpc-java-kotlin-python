// Examples: Clean Code
// Source: best-practices/SKILL.md

package com.example.app;

// ❌ Clean Code violado
class OrderProcessorBad {
    public void process(Order o) {
        // verifica se tem desconto
        if (o.hasDiscount()) {
            // calcula valor com desconto
            double v = o.getValue() * 0.9;
            // aplica
            o.setValue(v);
        }
        // salva
        repo.save(o);
    }
}

// ✅ Clean Code aplicado
class OrderProcessorGood {
    public void applyDiscount(Order order) {
        if (!order.hasDiscount()) {
            return;
        }
        var discountedValue = order.calculateDiscountedValue();
        order.updateValue(discountedValue);
        orderRepository.save(order);
    }
}

// Placeholder classes
class Order {
    boolean hasDiscount() { return false; }
    double getValue() { return 0; }
    void setValue(double v) {}
    double calculateDiscountedValue() { return 0; }
    void updateValue(double v) {}
}

class OrderRepository {
    void save(Order o) {}
}
