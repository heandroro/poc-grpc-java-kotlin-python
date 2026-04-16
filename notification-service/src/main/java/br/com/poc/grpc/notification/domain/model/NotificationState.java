package br.com.poc.grpc.notification.domain.model;

public enum NotificationState {
    UNSPECIFIED,
    PENDING,
    DELIVERED,
    ACKNOWLEDGED,
    FAILED
}
