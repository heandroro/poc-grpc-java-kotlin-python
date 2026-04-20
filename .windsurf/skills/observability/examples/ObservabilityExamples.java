import io.micrometer.core.instrument.*;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import io.micrometer.observation.annotation.Observed;
import io.micrometer.tracing.handler.DefaultTracingObservationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Queue;

// ============================================================
// Custom HealthIndicator — banco de dados
// ============================================================

@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    public DatabaseHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(1)) {
                return Health.up()
                    .withDetail("database", "PostgreSQL")
                    .withDetail("validationQuery", "SELECT 1")
                    .build();
            }
        } catch (SQLException e) {
            return Health.down().withException(e).build();
        }
        return Health.down().build();
    }
}

// ============================================================
// ReactiveHealthIndicator — API externa
// ============================================================

@Component
class ExternalApiHealthIndicator implements ReactiveHealthIndicator {

    private final WebClient webClient;

    ExternalApiHealthIndicator(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://api.external.com").build();
    }

    @Override
    public Mono<Health> health() {
        return webClient.get().uri("/health")
            .retrieve()
            .toBodilessEntity()
            .map(_ -> Health.up().withDetail("externalApi", "reachable").build())
            .onErrorResume(e -> Mono.just(Health.down().withException(e).build()));
    }
}

// ============================================================
// Métricas customizadas com Micrometer
// ============================================================

@Service
class OrderMetricsService {

    private static final Logger log = LoggerFactory.getLogger(OrderMetricsService.class);

    private final Counter orderCounter;
    private final Timer orderProcessingTimer;
    private final Queue<Object> orderQueue;

    OrderMetricsService(MeterRegistry meterRegistry, Queue<Object> orderQueue) {
        this.orderQueue = orderQueue;

        this.orderCounter = Counter.builder("orders.created")
            .description("Total orders created")
            .tag("type", "online")
            .register(meterRegistry);

        this.orderProcessingTimer = Timer.builder("orders.processing.time")
            .description("Order processing time")
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(meterRegistry);

        // Gauge — valor observado em tempo real
        Gauge.builder("orders.queue.size", orderQueue, Queue::size)
            .description("Current order queue size")
            .register(meterRegistry);
    }

    public void processOrder(Runnable logic) {
        orderProcessingTimer.record(() -> {
            logic.run();
            orderCounter.increment();
            log.info("Order processed");  // trace ID é adicionado automaticamente via MDC
        });
    }
}

// ============================================================
// Distributed Tracing com @Observed
// ============================================================

@Configuration
class TracingConfig {

    @Bean
    ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }
}

@Service
@Observed(name = "order.service", contextualName = "order-service")
class OrderService {
    // Todos os métodos públicos são automaticamente instrumentados com spans
}

// ============================================================
// ObservationFilter — adicionar dados de contexto a spans
// ============================================================

@Component
class UserObservationFilter implements io.micrometer.observation.ObservationFilter {

    @Override
    public Observation.Context map(Observation.Context context) {
        if (context instanceof io.micrometer.http.server.reactive.ServerRequestObservationContext ctx) {
            ctx.addHighCardinalityKeyValue(
                io.micrometer.common.KeyValue.of("user.id", getCurrentUserId())
            );
        }
        return context;
    }

    private String getCurrentUserId() {
        // extrair do SecurityContext
        return "anonymous";
    }
}
