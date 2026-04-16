package br.com.poc.grpc.notification.infrastructure.persistence;

import br.com.poc.grpc.notification.domain.model.Notification;
import br.com.poc.grpc.notification.domain.model.NotificationPriority;
import br.com.poc.grpc.notification.domain.model.NotificationState;
import br.com.poc.grpc.notification.domain.port.NotificationRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryNotificationRepository implements NotificationRepository {

    private final Map<String, Notification> store = new ConcurrentHashMap<>();

    @Override
    public Notification save(Notification notification) {
        store.put(notification.notificationId(), notification);
        return notification;
    }

    @Override
    public Optional<Notification> findById(String notificationId) {
        return Optional.ofNullable(store.get(notificationId));
    }

    @Override
    public List<Notification> findByUserIdAndTopics(String userId, List<String> topics, NotificationPriority minPriority) {
        return store.values().stream()
            .filter(n -> n.userId().equals(userId))
            .filter(n -> topics.isEmpty() || topics.contains(n.topic()))
            .filter(n -> n.priority().ordinal() >= minPriority.ordinal())
            .toList();
    }

    @Override
    public Notification updateState(String notificationId, NotificationState newState) {
        var existing = store.get(notificationId);
        if (existing == null) {
            throw new IllegalArgumentException("Notification not found: " + notificationId);
        }
        var updated = new Notification(
            existing.notificationId(),
            existing.userId(),
            existing.topic(),
            existing.title(),
            existing.body(),
            existing.priority(),
            newState,
            existing.createdAt(),
            existing.metadata()
        );
        store.put(notificationId, updated);
        return updated;
    }
}
