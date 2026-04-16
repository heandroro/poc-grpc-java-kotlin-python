package br.com.poc.grpc.notification.infrastructure.pubsub;

import br.com.poc.grpc.notification.domain.model.Notification;
import br.com.poc.grpc.notification.domain.model.NotificationPriority;
import br.com.poc.grpc.notification.domain.model.NotificationState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class InMemoryNotificationPublisherTest {

    private InMemoryNotificationPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new InMemoryNotificationPublisher();
    }

    @Test
    void should_deliver_notification_to_subscriber() {
        List<Notification> received = new ArrayList<>();
        publisher.subscribe(received::add);

        publisher.publish(notification("n-001"));

        assertThat(received).hasSize(1);
        assertThat(received.get(0).notificationId()).isEqualTo("n-001");
    }

    @Test
    void should_deliver_to_multiple_subscribers() {
        List<Notification> r1 = new ArrayList<>();
        List<Notification> r2 = new ArrayList<>();
        publisher.subscribe(r1::add);
        publisher.subscribe(r2::add);

        publisher.publish(notification("n-002"));

        assertThat(r1).hasSize(1);
        assertThat(r2).hasSize(1);
    }

    @Test
    void should_not_deliver_after_unsubscribe() {
        List<Notification> received = new ArrayList<>();
        publisher.subscribe(received::add);
        publisher.unsubscribe(received::add);

        publisher.publish(notification("n-003"));

        assertThat(received).isEmpty();
    }

    @Test
    void should_continue_delivering_when_one_subscriber_throws() {
        List<Notification> received = new ArrayList<>();
        publisher.subscribe(n -> {
            throw new RuntimeException("subscriber boom");
        });
        publisher.subscribe(received::add);

        assertThatCode(() -> publisher.publish(notification("n-004"))).doesNotThrowAnyException();

        assertThat(received).hasSize(1);
    }

    @Test
    void should_do_nothing_when_no_subscribers() {
        assertThatCode(() -> publisher.publish(notification("n-005"))).doesNotThrowAnyException();
    }

    private Notification notification(String id) {
        return new Notification(id, "user-001", "promos", "Title", "Body",
            NotificationPriority.HIGH, NotificationState.PENDING, Instant.now(), Map.of());
    }
}
