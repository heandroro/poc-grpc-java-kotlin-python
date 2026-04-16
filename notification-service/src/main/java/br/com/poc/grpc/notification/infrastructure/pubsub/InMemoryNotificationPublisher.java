package br.com.poc.grpc.notification.infrastructure.pubsub;

import br.com.poc.grpc.notification.domain.model.Notification;
import br.com.poc.grpc.notification.domain.port.NotificationPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Component
public class InMemoryNotificationPublisher implements NotificationPublisher {

    private static final Logger log = LoggerFactory.getLogger(InMemoryNotificationPublisher.class);

    private final List<Consumer<Notification>> subscribers = new CopyOnWriteArrayList<>();

    @Override
    public void publish(Notification notification) {
        log.debug("Publishing notification notificationId={}", notification.notificationId());
        subscribers.forEach(s -> {
            try {
                s.accept(notification);
            } catch (Exception e) {
                log.warn("Subscriber failed to process notification notificationId={} error={}", notification.notificationId(), e.getMessage());
            }
        });
    }

    public void subscribe(Consumer<Notification> consumer) {
        subscribers.add(consumer);
    }

    public void unsubscribe(Consumer<Notification> consumer) {
        subscribers.remove(consumer);
    }
}
