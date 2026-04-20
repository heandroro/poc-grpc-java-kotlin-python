// Example: Unit Test com JUnit 6, Mockito 5, AssertJ, BDDMockito
// Source: unit-test/SKILL.md

package com.example.app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void should_create_order_when_valid_request() {
        // given
        var request = new CreateOrderRequest("item-1", 2);
        given(orderRepository.save(any())).willReturn(new Order("id-1"));

        // when
        var result = orderService.create(request);

        // then
        assertThat(result.id()).isEqualTo("id-1");
        then(orderRepository).should().save(any());
    }
}

// ============================================================
// Instancio — geração automática de objetos complexos
// ============================================================

@ExtendWith(MockitoExtension.class)
class OrderServiceInstancioTest {

    @Mock private OrderRepository repository;
    @InjectMocks private OrderService service;

    @Test
    void should_process_order_with_random_data() {
        // given — objeto populado automaticamente com dados aleatórios
        var order = Instancio.create(Order.class);

        // given — com campo específico sobrescrito
        var pendingOrder = Instancio.of(Order.class)
            .set(field(Order::id), "fixed-id")
            .create();

        // given — coleção de objetos
        var orders = Instancio.createList(Order.class, 10);

        given(repository.save(any())).willReturn(order);

        var result = service.create(new CreateOrderRequest("item-1", 2));

        assertThat(result).isNotNull();
    }
}

// ============================================================
// DataFaker — dados realistas de negócio
// ============================================================

class OrderFixture {

    private static final Faker faker = new Faker(new java.util.Locale("pt", "BR"));

    static CreateOrderRequest validRequest() {
        return new CreateOrderRequest(
            faker.commerce().productName(),       // "Teclado Mecânico RGB"
            faker.number().numberBetween(1, 10)
        );
    }

    static String randomEmail()  { return faker.internet().emailAddress(); }
    static String randomCpf()    { return faker.cpf().valid(); }
    static String randomCnpj()   { return faker.cnpj().valid(); }
    static String randomPhone()  { return faker.phoneNumber().cellPhone(); }
}

// ============================================================
// Instancio + DataFaker combinados
// ============================================================

@ExtendWith(MockitoExtension.class)
class OrderNotificationTest {

    @Mock private OrderRepository repository;
    @Mock private EmailClient emailClient;
    @InjectMocks private OrderService service;

    @Test
    void should_send_email_when_order_confirmed() {
        var customerEmail = OrderFixture.randomEmail();

        // Instancio constrói o objeto; DataFaker provê dados realistas nos campos
        var order = Instancio.of(Order.class)
            .set(field(Order::id), "order-1")
            .create();

        given(repository.save(any())).willReturn(order);

        service.create(new CreateOrderRequest("item-1", 1));

        then(emailClient).should().send(argThat(email -> email != null));
    }
}

// ============================================================
// @Nested — organização de cenários
// ============================================================

@ExtendWith(MockitoExtension.class)
class OrderServiceNestedTest {

    @Mock private OrderRepository repository;
    @InjectMocks private OrderService service;

    @Nested
    class WhenCreatingOrder {

        @Test
        void should_save_with_pending_status() {
            given(repository.save(any())).willReturn(new Order("id-1"));
            var result = service.create(new CreateOrderRequest("item-1", 2));
            assertThat(result.id()).isEqualTo("id-1");
        }

        @Test
        void should_throw_when_quantity_is_zero() {
            assertThatThrownBy(() -> service.create(new CreateOrderRequest("item-1", 0)))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }
}

// ============================================================
// Placeholder classes
class OrderRepository {
    Order save(Order order) { return order; }
}

class OrderService {
    private final OrderRepository repository;
    OrderService(OrderRepository repo) { this.repository = repo; }
    Order create(CreateOrderRequest req) { return repository.save(new Order("id-1")); }
}

record CreateOrderRequest(String item, int quantity) {}
record Order(String id) {}
