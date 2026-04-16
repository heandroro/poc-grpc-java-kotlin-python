import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

// ============================================================
// Gatling 4 — Simulação de carga para /api/orders
// ============================================================

public class OrderSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http
        .baseUrl("http://localhost:8080")
        .acceptHeader("application/json")
        .contentTypeHeader("application/json");

    // Cenário: criar pedido
    ScenarioBuilder createOrder = scenario("Create Order")
        .exec(http("POST /api/orders")
            .post("/api/orders")
            .body(StringBody("""
                {"customerId": "cust-1", "items": [{"productId": "prod-1", "quantity": 2}]}
                """))
            .check(status().is(201))
            .check(jsonPath("$.id").saveAs("orderId")))
        .pause(Duration.ofMillis(100))
        .exec(http("GET /api/orders/{id}")
            .get("/api/orders/#{orderId}")
            .check(status().is(200)));

    // Cenário: consultar pedidos
    ScenarioBuilder listOrders = scenario("List Orders")
        .exec(http("GET /api/orders")
            .get("/api/orders")
            .check(status().is(200)));

    {
        setUp(
            // Load test: carga esperada em produção
            createOrder.injectOpen(
                rampUsers(50).during(Duration.ofSeconds(30)),
                constantUsersPerSec(20).during(Duration.ofMinutes(2))
            ),
            listOrders.injectOpen(
                rampUsers(100).during(Duration.ofSeconds(30))
            )
        )
        .protocols(httpProtocol)
        .assertions(
            global().responseTime().percentile(99).lt(500),    // p99 ≤ 500ms
            global().responseTime().percentile(95).lt(300),    // p95 ≤ 300ms
            global().successfulRequests().percent().gt(99.9),  // error rate ≤ 0.1%
            global().requestsPerSec().gt(1000.0)               // throughput ≥ 1000 req/s
        );
    }
}
