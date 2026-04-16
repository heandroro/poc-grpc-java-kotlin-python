package br.com.poc.grpc.notification.infrastructure.persistence;

import br.com.poc.grpc.notification.domain.model.Notification;
import br.com.poc.grpc.notification.domain.model.NotificationPriority;
import br.com.poc.grpc.notification.domain.model.NotificationState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryNotificationRepositoryTest {

    private InMemoryNotificationRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryNotificationRepository();
    }

    @Test
    void should_save_and_find_by_id() {
        var n = notification("n-001", "user-001", "promos", NotificationPriority.HIGH);

        repository.save(n);
        var found = repository.findById("n-001");

        assertThat(found).isPresent().contains(n);
    }

    @Test
    void should_return_empty_optional_when_not_found() {
        assertThat(repository.findById("missing")).isEmpty();
    }

    @Test
    void should_overwrite_on_duplicate_save() {
        var n = notification("n-001", "user-001", "promos", NotificationPriority.HIGH);
        var updated = new Notification("n-001", "user-001", "promos", "New Title", "New Body",
            NotificationPriority.CRITICAL, NotificationState.DELIVERED, Instant.now(), Map.of());

        repository.save(n);
        repository.save(updated);

        assertThat(repository.findById("n-001")).isPresent()
            .hasValueSatisfying(r -> assertThat(r.title()).isEqualTo("New Title"));
    }

    @Test
    void should_find_by_user_id_matching_topics() {
        repository.save(notification("n-001", "user-001", "promos", NotificationPriority.HIGH));
        repository.save(notification("n-002", "user-001", "alerts", NotificationPriority.NORMAL));
        repository.save(notification("n-003", "user-002", "promos", NotificationPriority.HIGH));

        var results = repository.findByUserIdAndTopics("user-001", List.of("promos"), NotificationPriority.LOW);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).notificationId()).isEqualTo("n-001");
    }

    @Test
    void should_find_all_topics_when_list_is_empty() {
        repository.save(notification("n-001", "user-001", "promos", NotificationPriority.HIGH));
        repository.save(notification("n-002", "user-001", "alerts", NotificationPriority.NORMAL));

        var results = repository.findByUserIdAndTopics("user-001", List.of(), NotificationPriority.LOW);

        assertThat(results).hasSize(2);
    }

    @Test
    void should_filter_by_min_priority() {
        repository.save(notification("n-001", "user-001", "promos", NotificationPriority.LOW));
        repository.save(notification("n-002", "user-001", "promos", NotificationPriority.HIGH));

        var results = repository.findByUserIdAndTopics("user-001", List.of(), NotificationPriority.HIGH);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).notificationId()).isEqualTo("n-002");
    }

    @Test
    void should_update_state_successfully() {
        repository.save(notification("n-001", "user-001", "promos", NotificationPriority.HIGH));

        var updated = repository.updateState("n-001", NotificationState.DELIVERED);

        assertThat(updated.state()).isEqualTo(NotificationState.DELIVERED);
        assertThat(repository.findById("n-001")).isPresent()
            .hasValueSatisfying(r -> assertThat(r.state()).isEqualTo(NotificationState.DELIVERED));
    }

    @Test
    void should_throw_when_updating_state_of_nonexistent_notification() {
        assertThatThrownBy(() -> repository.updateState("missing", NotificationState.DELIVERED))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("missing");
    }

    private Notification notification(String id, String userId, String topic, NotificationPriority priority) {
        return new Notification(id, userId, topic, "Title", "Body", priority,
            NotificationState.PENDING, Instant.now(), Map.of());
    }
}
