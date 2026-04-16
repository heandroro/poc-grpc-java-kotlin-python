import java.net.URI;
import java.util.List;
import java.util.UUID;

// ============================================================
// 1. Hierarquia de DomainException
// ============================================================

abstract class DomainException extends RuntimeException {
    private final String errorCode;
    private final int httpStatus;

    protected DomainException(String message, String errorCode, int httpStatus) {
        super(message);
        this.errorCode  = errorCode;
        this.httpStatus = httpStatus;
    }

    String errorCode()  { return errorCode; }
    int    httpStatus() { return httpStatus; }
}

class OrderNotFoundException extends DomainException {
    OrderNotFoundException(UUID orderId) {
        super("Pedido não encontrado: " + orderId, "ORDER_001", 404);
    }
}

class InsufficientStockException extends DomainException {
    private final String productId;
    private final int    requested;
    private final int    available;

    InsufficientStockException(String productId, int requested, int available) {
        super("Estoque insuficiente para " + productId, "STOCK_002", 409);
        this.productId = productId;
        this.requested = requested;
        this.available = available;
    }

    String productId() { return productId; }
    int    requested() { return requested; }
    int    available() { return available; }
}

class OrderAlreadyConfirmedException extends DomainException {
    OrderAlreadyConfirmedException(UUID orderId) {
        super("Pedido " + orderId + " já foi confirmado", "ORDER_003", 409);
    }
}

// ============================================================
// 2. Validação de negócio — exceptions com contexto rico
// ============================================================

class OrderService {

    Order confirmOrder(UUID orderId) {
        var order = findOrder(orderId);

        if (order.isConfirmed()) {
            throw new OrderAlreadyConfirmedException(orderId);  // ✅ contexto no construtor
        }

        order.confirm();
        return order;
    }

    void reserveStock(String productId, int quantity, int available) {
        if (quantity > available) {
            throw new InsufficientStockException(productId, quantity, available);  // ✅ dados da falha
        }
    }

    private Order findOrder(UUID id) {
        // simulado para exemplo
        return null;
    }
}

// ============================================================
// 3. Anti-patterns — NÃO fazer
// ============================================================

class BadExceptionExamples {

    // ❌ Exceção genérica sem contexto
    void badExample1(UUID orderId) {
        throw new RuntimeException("error");  // sem código, sem contexto
    }

    // ❌ Swallow silencioso
    void badExample2() {
        try {
            riskyOperation();
        } catch (Exception e) {
            // nada — exceção engolida
        }
    }

    // ❌ Logar e relançar — duplica o log
    void badExample3() {
        try {
            riskyOperation();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());  // log aqui
            throw new RuntimeException(e);                   // e relança — dois logs
        }
    }

    // ✅ Correto: relançar sem logar (deixar o handler global logar)
    void goodExample3() throws Exception {
        try {
            riskyOperation();
        } catch (Exception e) {
            throw new DomainSpecificException("Falha na operação", e);
        }
    }

    private void riskyOperation() throws Exception {}
}

class DomainSpecificException extends RuntimeException {
    DomainSpecificException(String message, Throwable cause) { super(message, cause); }
}

// ============================================================
// Stubs auxiliares para compilação do exemplo
// ============================================================

class Order {
    boolean isConfirmed() { return false; }
    void confirm()        {}
}
