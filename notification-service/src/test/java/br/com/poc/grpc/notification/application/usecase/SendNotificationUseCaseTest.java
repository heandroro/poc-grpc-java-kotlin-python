package br.com.poc.grpc.notification.application.usecase;

import br.com.poc.grpc.notification.application.dto.SendNotificationCommand;
import br.com.poc.grpc.notification.domain.model.Notification;
import br.com.poc.grpc.notification.domain.model.NotificationPriority;
import br.com.poc.grpc.notification.domain.model.NotificationState;
import br.com.poc.grpc.notification.domain.port.NotificationPublisher;
import br.com.poc.grpc.notification.domain.port.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SendNotificationUseCaseTest {

    @Mock
    private NotificationRepository repository;

    @Mock
    private NotificationPublisher publisher;

    private SendNotificationUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new SendNotificationUseCase(repository, publisher);
    }

    @Test
    void should_save_and_publish_when_command_is_valid() {
        var command = new SendNotificationCommand(
            "user-001", "promos", "Flash Sale", "50% off", NotificationPriority.HIGH, Map.of("key", "value")
        );

        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(command);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(repository).save(captor.capture());
        verify(publisher).publish(captor.getValue());

        assertThat(result.userId()).isEqualTo("user-001");
        assertThat(result.topic()).isEqualTo("promos");
        assertThat(result.title()).isEqualTo("Flash Sale");
        assertThat(result.body()).isEqualTo("50% off");
        assertThat(result.priority()).isEqualTo(NotificationPriority.HIGH);
        assertThat(result.state()).isEqualTo(NotificationState.PENDING);
        assertThat(result.notificationId()).isNotBlank();
        assertThat(result.createdAt()).isBefore(Instant.now().plusSeconds(1));
        assertThat(result.metadata()).containsEntry("key", "value");
    }

    @Test
    void should_return_saved_notification_from_repository() {
        var command = new SendNotificationCommand(
            "user-002", "alerts", "Alert", "Body", NotificationPriority.CRITICAL, Map.of()
        );
        var saved = new Notification("fixed-id", "user-002", "alerts", "Alert", "Body",
            NotificationPriority.CRITICAL, NotificationState.PENDING, Instant.now(), Map.of());

        when(repository.save(any())).thenReturn(saved);

        var result = useCase.execute(command);

        assertThat(result.notificationId()).isEqualTo("fixed-id");
    }
}
