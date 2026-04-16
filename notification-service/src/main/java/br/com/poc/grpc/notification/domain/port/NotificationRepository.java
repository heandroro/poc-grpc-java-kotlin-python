package br.com.poc.grpc.notification.domain.port;

import br.com.poc.grpc.notification.domain.model.Notification;
import br.com.poc.grpc.notification.domain.model.NotificationPriority;
import br.com.poc.grpc.notification.domain.model.NotificationState;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    Notification save(Notification notification);
    Optional<Notification> findById(String notificationId);
    List<Notification> findByUserIdAndTopics(String userId, List<String> topics, NotificationPriority minPriority);
    Notification updateState(String notificationId, NotificationState newState);
}
