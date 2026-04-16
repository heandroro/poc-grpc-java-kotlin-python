package br.com.poc.grpc.notification.adapter.grpc;

import br.com.poc.grpc.notification.domain.model.Notification;
import br.com.poc.grpc.notification.domain.model.NotificationPriority;
import br.com.poc.grpc.notification.domain.model.NotificationState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationProtoMapperTest {

    private NotificationProtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new NotificationProtoMapper();
    }

    @Test
    void should_map_domain_notification_to_proto() {
        var domain = new Notification(
            "n-001", "user-001", "promos", "Title", "Body",
            NotificationPriority.HIGH, NotificationState.DELIVERED,
            Instant.ofEpochSecond(1700000000), Map.of("k", "v")
        );

        var proto = mapper.toProto(domain);

        assertThat(proto.getNotificationId()).isEqualTo("n-001");
        assertThat(proto.getUserId()).isEqualTo("user-001");
        assertThat(proto.getTopic()).isEqualTo("promos");
        assertThat(proto.getTitle()).isEqualTo("Title");
        assertThat(proto.getBody()).isEqualTo("Body");
        assertThat(proto.getPriority()).isEqualTo(br.com.poc.grpc.notification.v1.NotificationPriority.NOTIFICATION_PRIORITY_HIGH);
        assertThat(proto.getState()).isEqualTo(br.com.poc.grpc.notification.v1.NotificationState.NOTIFICATION_STATE_DELIVERED);
        assertThat(proto.getCreatedAt().getSeconds()).isEqualTo(1700000000L);
        assertThat(proto.getMetadataMap()).containsEntry("k", "v");
    }

    @ParameterizedTest
    @EnumSource(value = br.com.poc.grpc.notification.v1.NotificationPriority.class,
        names = {"NOTIFICATION_PRIORITY_LOW", "NOTIFICATION_PRIORITY_NORMAL",
            "NOTIFICATION_PRIORITY_HIGH", "NOTIFICATION_PRIORITY_CRITICAL",
            "NOTIFICATION_PRIORITY_UNSPECIFIED"})
    void should_map_all_proto_priorities_to_domain(
        br.com.poc.grpc.notification.v1.NotificationPriority proto
    ) {
        var domain = mapper.toDomainPriority(proto);
        assertThat(domain).isNotNull();
    }

    @Test
    void should_map_proto_priority_low_to_domain() {
        assertThat(mapper.toDomainPriority(
            br.com.poc.grpc.notification.v1.NotificationPriority.NOTIFICATION_PRIORITY_LOW))
            .isEqualTo(NotificationPriority.LOW);
    }

    @Test
    void should_map_proto_priority_normal_to_domain() {
        assertThat(mapper.toDomainPriority(
            br.com.poc.grpc.notification.v1.NotificationPriority.NOTIFICATION_PRIORITY_NORMAL))
            .isEqualTo(NotificationPriority.NORMAL);
    }

    @Test
    void should_map_proto_priority_high_to_domain() {
        assertThat(mapper.toDomainPriority(
            br.com.poc.grpc.notification.v1.NotificationPriority.NOTIFICATION_PRIORITY_HIGH))
            .isEqualTo(NotificationPriority.HIGH);
    }

    @Test
    void should_map_proto_priority_critical_to_domain() {
        assertThat(mapper.toDomainPriority(
            br.com.poc.grpc.notification.v1.NotificationPriority.NOTIFICATION_PRIORITY_CRITICAL))
            .isEqualTo(NotificationPriority.CRITICAL);
    }

    @Test
    void should_map_proto_priority_unspecified_to_domain() {
        assertThat(mapper.toDomainPriority(
            br.com.poc.grpc.notification.v1.NotificationPriority.NOTIFICATION_PRIORITY_UNSPECIFIED))
            .isEqualTo(NotificationPriority.UNSPECIFIED);
    }

    @Test
    void should_map_all_domain_priorities_to_proto() {
        assertThat(mapper.toProtoPriority(NotificationPriority.LOW))
            .isEqualTo(br.com.poc.grpc.notification.v1.NotificationPriority.NOTIFICATION_PRIORITY_LOW);
        assertThat(mapper.toProtoPriority(NotificationPriority.NORMAL))
            .isEqualTo(br.com.poc.grpc.notification.v1.NotificationPriority.NOTIFICATION_PRIORITY_NORMAL);
        assertThat(mapper.toProtoPriority(NotificationPriority.HIGH))
            .isEqualTo(br.com.poc.grpc.notification.v1.NotificationPriority.NOTIFICATION_PRIORITY_HIGH);
        assertThat(mapper.toProtoPriority(NotificationPriority.CRITICAL))
            .isEqualTo(br.com.poc.grpc.notification.v1.NotificationPriority.NOTIFICATION_PRIORITY_CRITICAL);
        assertThat(mapper.toProtoPriority(NotificationPriority.UNSPECIFIED))
            .isEqualTo(br.com.poc.grpc.notification.v1.NotificationPriority.NOTIFICATION_PRIORITY_UNSPECIFIED);
    }

    @Test
    void should_map_all_proto_states_to_domain() {
        assertThat(mapper.toDomainState(
            br.com.poc.grpc.notification.v1.NotificationState.NOTIFICATION_STATE_PENDING))
            .isEqualTo(NotificationState.PENDING);
        assertThat(mapper.toDomainState(
            br.com.poc.grpc.notification.v1.NotificationState.NOTIFICATION_STATE_DELIVERED))
            .isEqualTo(NotificationState.DELIVERED);
        assertThat(mapper.toDomainState(
            br.com.poc.grpc.notification.v1.NotificationState.NOTIFICATION_STATE_ACKNOWLEDGED))
            .isEqualTo(NotificationState.ACKNOWLEDGED);
        assertThat(mapper.toDomainState(
            br.com.poc.grpc.notification.v1.NotificationState.NOTIFICATION_STATE_FAILED))
            .isEqualTo(NotificationState.FAILED);
        assertThat(mapper.toDomainState(
            br.com.poc.grpc.notification.v1.NotificationState.NOTIFICATION_STATE_UNSPECIFIED))
            .isEqualTo(NotificationState.UNSPECIFIED);
    }

    @Test
    void should_map_all_domain_states_to_proto() {
        assertThat(mapper.toProtoState(NotificationState.PENDING))
            .isEqualTo(br.com.poc.grpc.notification.v1.NotificationState.NOTIFICATION_STATE_PENDING);
        assertThat(mapper.toProtoState(NotificationState.DELIVERED))
            .isEqualTo(br.com.poc.grpc.notification.v1.NotificationState.NOTIFICATION_STATE_DELIVERED);
        assertThat(mapper.toProtoState(NotificationState.ACKNOWLEDGED))
            .isEqualTo(br.com.poc.grpc.notification.v1.NotificationState.NOTIFICATION_STATE_ACKNOWLEDGED);
        assertThat(mapper.toProtoState(NotificationState.FAILED))
            .isEqualTo(br.com.poc.grpc.notification.v1.NotificationState.NOTIFICATION_STATE_FAILED);
        assertThat(mapper.toProtoState(NotificationState.UNSPECIFIED))
            .isEqualTo(br.com.poc.grpc.notification.v1.NotificationState.NOTIFICATION_STATE_UNSPECIFIED);
    }
}
