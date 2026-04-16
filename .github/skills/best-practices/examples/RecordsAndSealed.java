// Examples: Records, Sealed Classes, Pattern Matching
// Source: best-practices/SKILL.md

package com.example.app;

import java.time.Instant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

// Record como DTO imutável
public record CreateOrderRequest(
    @NotBlank String item,
    @Positive int quantity
) {}

// Sealed interface para estados de domínio
public sealed interface OrderStatus
    permits Pending, Confirmed, Shipped, Delivered {

    record Pending() implements OrderStatus {}
    record Confirmed(Instant at) implements OrderStatus {}
    record Shipped(String trackingCode) implements OrderStatus {}
    record Delivered(Instant at) implements OrderStatus {}
}

// Pattern matching em switch
class OrderStatusDescriber {
    public String describe(OrderStatus status) {
        return switch (status) {
            case Pending _         -> "Aguardando confirmação";
            case Confirmed(var at) -> "Confirmado em " + at;
            case Shipped(var code) -> "Enviado: " + code;
            case Delivered(var at) -> "Entregue em " + at;
        };
    }
}

// Configuration Properties como record
record OrderProperties(
    int maxItems,
    java.time.Duration timeout,
    RetryProperties retry
) {
    public record RetryProperties(int maxAttempts, java.time.Duration backoff) {}
}
