package br.com.poc.grpc.notification.adapter.grpc;

import br.com.poc.grpc.notification.domain.model.NotificationPriority;
import br.com.poc.grpc.notification.domain.model.NotificationState;
import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;

@Component
public class NotificationProtoMapper {

    public br.com.poc.grpc.notification.v1.Notification toProto(br.com.poc.grpc.notification.domain.model.Notification domain) {
        var createdAt = domain.createdAt();
        return br.com.poc.grpc.notification.v1.Notification.newBuilder()
            .setNotificationId(domain.notificationId())
            .setUserId(domain.userId())
            .setTopic(domain.topic())
            .setTitle(domain.title())
            .setBody(domain.body())
            .setPriority(toProtoPriority(domain.priority()))
            .setState(toProtoState(domain.state()))
            .setCreatedAt(Timestamp.newBuilder()
                .setSeconds(createdAt.getEpochSecond())
                .setNanos(createdAt.getNano())
                .build())
            .putAllMetadata(domain.metadata())
            .build();
    }

    public NotificationPriority toDomainPriority(br.com.poc.grpc.notification.v1.NotificationPriority proto) {
        return switch (proto) {
            case NOTIFICATION_PRIORITY_LOW -> NotificationPriority.LOW;
            case NOTIFICATION_PRIORITY_NORMAL -> NotificationPriority.NORMAL;
            case NOTIFICATION_PRIORITY_HIGH -> NotificationPriority.HIGH;
            case NOTIFICATION_PRIORITY_CRITICAL -> NotificationPriority.CRITICAL;
            default -> NotificationPriority.UNSPECIFIED;
        };
    }

    public NotificationState toDomainState(br.com.poc.grpc.notification.v1.NotificationState proto) {
        return switch (proto) {
            case NOTIFICATION_STATE_PENDING -> NotificationState.PENDING;
            case NOTIFICATION_STATE_DELIVERED -> NotificationState.DELIVERED;
            case NOTIFICATION_STATE_ACKNOWLEDGED -> NotificationState.ACKNOWLEDGED;
            case NOTIFICATION_STATE_FAILED -> NotificationState.FAILED;
            default -> NotificationState.UNSPECIFIED;
        };
    }

    public br.com.poc.grpc.notification.v1.NotificationPriority toProtoPriority(NotificationPriority domain) {
        return switch (domain) {
            case LOW -> br.com.poc.grpc.notification.v1.NotificationPriority.NOTIFICATION_PRIORITY_LOW;
            case NORMAL -> br.com.poc.grpc.notification.v1.NotificationPriority.NOTIFICATION_PRIORITY_NORMAL;
            case HIGH -> br.com.poc.grpc.notification.v1.NotificationPriority.NOTIFICATION_PRIORITY_HIGH;
            case CRITICAL -> br.com.poc.grpc.notification.v1.NotificationPriority.NOTIFICATION_PRIORITY_CRITICAL;
            default -> br.com.poc.grpc.notification.v1.NotificationPriority.NOTIFICATION_PRIORITY_UNSPECIFIED;
        };
    }

    public br.com.poc.grpc.notification.v1.NotificationState toProtoState(NotificationState domain) {
        return switch (domain) {
            case PENDING -> br.com.poc.grpc.notification.v1.NotificationState.NOTIFICATION_STATE_PENDING;
            case DELIVERED -> br.com.poc.grpc.notification.v1.NotificationState.NOTIFICATION_STATE_DELIVERED;
            case ACKNOWLEDGED -> br.com.poc.grpc.notification.v1.NotificationState.NOTIFICATION_STATE_ACKNOWLEDGED;
            case FAILED -> br.com.poc.grpc.notification.v1.NotificationState.NOTIFICATION_STATE_FAILED;
            default -> br.com.poc.grpc.notification.v1.NotificationState.NOTIFICATION_STATE_UNSPECIFIED;
        };
    }
}
