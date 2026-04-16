package br.com.poc.grpc.notification.application.usecase;

import br.com.poc.grpc.notification.application.dto.SendNotificationCommand;
import br.com.poc.grpc.notification.domain.model.Notification;
import br.com.poc.grpc.notification.domain.model.NotificationState;
import br.com.poc.grpc.notification.domain.port.NotificationPublisher;
import br.com.poc.grpc.notification.domain.port.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class SendNotificationUseCase {

    private static final Logger log = LoggerFactory.getLogger(SendNotificationUseCase.class);

    private final NotificationRepository repository;
    private final NotificationPublisher publisher;

    public SendNotificationUseCase(NotificationRepository repository, NotificationPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public Notification execute(SendNotificationCommand command) {
        var notification = new Notification(
            UUID.randomUUID().toString(),
            command.userId(),
            command.topic(),
            command.title(),
            command.body(),
            command.priority(),
            NotificationState.PENDING,
            Instant.now(),
            command.metadata()
        );
        var saved = repository.save(notification);
        publisher.publish(saved);
        log.info("Notification dispatched notificationId={} userId={} topic={}", saved.notificationId(), saved.userId(), saved.topic());
        return saved;
    }
}
