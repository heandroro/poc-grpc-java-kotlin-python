package br.com.poc.grpc.notification.domain.port;

import br.com.poc.grpc.notification.domain.model.Notification;

public interface NotificationPublisher {
    void publish(Notification notification);
}
