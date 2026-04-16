package br.com.poc.grpc.notification.domain.model;

import java.time.Instant;
import java.util.Map;

public record Notification(
    String notificationId,
    String userId,
    String topic,
    String title,
    String body,
    NotificationPriority priority,
    NotificationState state,
    Instant createdAt,
    Map<String, String> metadata
) {}
