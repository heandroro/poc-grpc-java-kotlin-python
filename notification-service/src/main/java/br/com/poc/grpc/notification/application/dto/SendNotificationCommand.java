package br.com.poc.grpc.notification.application.dto;

import br.com.poc.grpc.notification.domain.model.NotificationPriority;

import java.util.Map;

public record SendNotificationCommand(
    String userId,
    String topic,
    String title,
    String body,
    NotificationPriority priority,
    Map<String, String> metadata
) {}
