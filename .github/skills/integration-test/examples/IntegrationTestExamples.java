import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

// ============================================================
// @SpringBootTest + Testcontainers + WebTestClient
// ============================================================

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
class OrderApiIntegrationTest {

    @Container
    @ServiceConnection      // injeta spring.datasource.* automaticamente — sem @DynamicPropertySource
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void should_create_order_and_return_201() {
        var request = new CreateOrderRequest("item-1", 2);

        webTestClient.post().uri("/api/orders")
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.id").isNotEmpty()
            .jsonPath("$.status").isEqualTo("PENDING");
    }

    @Test
    void should_return_404_when_order_not_found() {
        webTestClient.get().uri("/api/orders/non-existent-id")
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("$.type").isNotEmpty();    // ProblemDetail RFC 9457
    }
}

// ============================================================
// WireMock — mock de API HTTP externa
// ============================================================

@WireMockTest(httpPort = 8089)
class PaymentGatewayClientTest {

    @Autowired
    private PaymentGatewayClient client;

    @Test
    void should_process_payment_when_gateway_approves() {
        stubFor(post(urlEqualTo("/payments"))
            .willReturn(okJson("""
                { "status": "approved", "transactionId": "txn-123" }
                """)));

        var result = client.processPayment(new PaymentRequest(100.0, "BRL"));

        assertThat(result.status()).isEqualTo("approved");
        assertThat(result.transactionId()).isEqualTo("txn-123");
    }

    @Test
    void should_throw_when_gateway_returns_500() {
        stubFor(post(urlEqualTo("/payments"))
            .willReturn(serverError()));

        assertThatThrownBy(() -> client.processPayment(new PaymentRequest(100.0, "BRL")))
            .isInstanceOf(PaymentGatewayException.class);
    }
}

// ============================================================
// @DataJpaTest — teste de slice para repositórios
// ============================================================

@DataJpaTest
@Testcontainers
class OrderRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private OrderRepository repository;

    @Test
    void should_persist_and_find_order() {
        var order = Order.create(new CustomerId(UUID.randomUUID()), OrderItems.of("item-1", 2));
        repository.save(order);

        var found = repository.findById(order.id());

        assertThat(found).isPresent();
        assertThat(found.get().status()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    @Sql("/test-data/orders.sql")   // setup com script SQL
    void should_find_pending_orders() {
        var orders = repository.findByStatus(OrderStatus.PENDING);
        assertThat(orders).isNotEmpty();
    }
}
