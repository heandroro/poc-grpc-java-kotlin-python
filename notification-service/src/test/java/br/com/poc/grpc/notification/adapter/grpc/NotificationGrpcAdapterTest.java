package br.com.poc.grpc.notification.adapter.grpc;

import br.com.poc.grpc.notification.application.usecase.SendNotificationUseCase;
import br.com.poc.grpc.notification.domain.model.Notification;
import br.com.poc.grpc.notification.domain.model.NotificationPriority;
import br.com.poc.grpc.notification.domain.model.NotificationState;
import br.com.poc.grpc.notification.infrastructure.pubsub.InMemoryNotificationPublisher;
import br.com.poc.grpc.notification.v1.NotificationAck;
import br.com.poc.grpc.notification.v1.SendNotificationRequest;
import br.com.poc.grpc.notification.v1.StreamNotificationsRequest;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationGrpcAdapterTest {

    @Mock
    private SendNotificationUseCase useCase;

    @Mock
    private InMemoryNotificationPublisher publisher;

    @Mock
    private NotificationProtoMapper mapper;

    @Mock
    private StreamObserver<br.com.poc.grpc.notification.v1.SendNotificationResponse> responseObserver;

    private NotificationGrpcAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new NotificationGrpcAdapter(useCase, publisher, mapper);
    }

    @Test
    void should_return_error_when_user_id_is_blank() {
        var request = SendNotificationRequest.newBuilder().setUserId("").setTopic("promos").build();

        adapter.sendNotification(request, responseObserver);

        ArgumentCaptor<StatusRuntimeException> captor = ArgumentCaptor.forClass(StatusRuntimeException.class);
        verify(responseObserver).onError(captor.capture());
        assertThat(captor.getValue().getStatus().getCode()).isEqualTo(Status.INVALID_ARGUMENT.getCode());
        verify(useCase, never()).execute(any());
    }

    @Test
    void should_send_notification_and_complete_when_request_is_valid() {
        var request = SendNotificationRequest.newBuilder()
            .setUserId("user-001")
            .setTopic("promos")
            .setTitle("Flash")
            .setBody("50% off")
            .build();

        var domainNotification = new Notification(
            "n-123", "user-001", "promos", "Flash", "50% off",
            NotificationPriority.NORMAL, NotificationState.PENDING, Instant.now(), Map.of()
        );

        when(mapper.toDomainPriority(any())).thenReturn(NotificationPriority.NORMAL);
        when(useCase.execute(any())).thenReturn(domainNotification);

        adapter.sendNotification(request, responseObserver);

        verify(useCase).execute(any());
        verify(responseObserver).onNext(any());
        verify(responseObserver).onCompleted();
    }

    @Test
    @SuppressWarnings("unchecked")
    void should_register_subscriber_and_forward_matching_notification() {
        var serverObserver = mock(ServerCallStreamObserver.class);
        var request = StreamNotificationsRequest.newBuilder()
            .setUserId("user-001")
            .addAllTopics(List.of("promos"))
            .build();

        when(mapper.toDomainPriority(any())).thenReturn(NotificationPriority.UNSPECIFIED);
        when(serverObserver.isCancelled()).thenReturn(false);
        when(mapper.toProto(any())).thenReturn(br.com.poc.grpc.notification.v1.Notification.getDefaultInstance());

        adapter.streamNotifications(request, serverObserver);

        ArgumentCaptor<Consumer> captor = ArgumentCaptor.forClass(Consumer.class);
        verify(publisher).subscribe(captor.capture());

        var matchingNotification = new Notification(
            "n-001", "user-001", "promos", "T", "B",
            NotificationPriority.HIGH, NotificationState.PENDING, Instant.now(), Map.of()
        );
        captor.getValue().accept(matchingNotification);

        verify(serverObserver).onNext(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void should_skip_notification_for_different_user() {
        var serverObserver = mock(ServerCallStreamObserver.class);
        var request = StreamNotificationsRequest.newBuilder().setUserId("user-001").build();

        when(mapper.toDomainPriority(any())).thenReturn(NotificationPriority.UNSPECIFIED);

        adapter.streamNotifications(request, serverObserver);

        ArgumentCaptor<Consumer> captor = ArgumentCaptor.forClass(Consumer.class);
        verify(publisher).subscribe(captor.capture());

        var wrongUser = new Notification(
            "n-002", "user-999", "promos", "T", "B",
            NotificationPriority.HIGH, NotificationState.PENDING, Instant.now(), Map.of()
        );
        captor.getValue().accept(wrongUser);

        verify(serverObserver, never()).onNext(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void should_unsubscribe_on_cancel() {
        var serverObserver = mock(ServerCallStreamObserver.class);
        var request = StreamNotificationsRequest.newBuilder().setUserId("user-001").build();
        when(mapper.toDomainPriority(any())).thenReturn(NotificationPriority.UNSPECIFIED);

        adapter.streamNotifications(request, serverObserver);

        ArgumentCaptor<Runnable> cancelCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(serverObserver).setOnCancelHandler(cancelCaptor.capture());
        cancelCaptor.getValue().run();

        verify(publisher).unsubscribe(any());
    }

    @Test
    void should_handle_notification_channel_ack_and_complete() {
        var statusObserver = mock(StreamObserver.class);

        var requestObserver = adapter.notificationChannel(statusObserver);

        var ack = NotificationAck.newBuilder()
            .setNotificationId("n-001")
            .setUserId("user-001")
            .build();

        requestObserver.onNext(ack);
        verify(statusObserver).onNext(any());

        requestObserver.onCompleted();
        verify(statusObserver).onCompleted();
    }

    @Test
    void should_handle_notification_channel_error_gracefully() {
        var statusObserver = mock(StreamObserver.class);
        var requestObserver = adapter.notificationChannel(statusObserver);

        requestObserver.onError(new RuntimeException("client disconnected"));
    }
}
