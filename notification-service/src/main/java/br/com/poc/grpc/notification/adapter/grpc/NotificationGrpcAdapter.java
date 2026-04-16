package br.com.poc.grpc.notification.adapter.grpc;

import br.com.poc.grpc.notification.application.dto.SendNotificationCommand;
import br.com.poc.grpc.notification.application.usecase.SendNotificationUseCase;
import br.com.poc.grpc.notification.infrastructure.grpc.interceptor.JwtServerInterceptor;
import br.com.poc.grpc.notification.infrastructure.pubsub.InMemoryNotificationPublisher;
import br.com.poc.grpc.notification.v1.NotificationAck;
import br.com.poc.grpc.notification.v1.NotificationServiceGrpc;
import br.com.poc.grpc.notification.v1.SendNotificationRequest;
import br.com.poc.grpc.notification.v1.SendNotificationResponse;
import br.com.poc.grpc.notification.v1.StreamNotificationsRequest;
import com.google.protobuf.Timestamp;
import io.grpc.Status;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.function.Consumer;

@GrpcService
public class NotificationGrpcAdapter extends NotificationServiceGrpc.NotificationServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(NotificationGrpcAdapter.class);

    private final SendNotificationUseCase sendNotificationUseCase;
    private final InMemoryNotificationPublisher publisher;
    private final NotificationProtoMapper mapper;

    public NotificationGrpcAdapter(
        SendNotificationUseCase sendNotificationUseCase,
        InMemoryNotificationPublisher publisher,
        NotificationProtoMapper mapper
    ) {
        this.sendNotificationUseCase = sendNotificationUseCase;
        this.publisher = publisher;
        this.mapper = mapper;
    }

    @Override
    public void sendNotification(SendNotificationRequest request, StreamObserver<SendNotificationResponse> responseObserver) {
        String callerId = JwtServerInterceptor.USER_ID_CTX_KEY.get();
        log.info("SendNotification callerId={} targetUserId={} topic={}", callerId, request.getUserId(), request.getTopic());

        if (request.getUserId().isBlank()) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription("user_id must not be blank")
                    .asRuntimeException()
            );
            return;
        }

        var command = new SendNotificationCommand(
            request.getUserId(),
            request.getTopic(),
            request.getTitle(),
            request.getBody(),
            mapper.toDomainPriority(request.getPriority()),
            request.getMetadataMap()
        );

        var notification = sendNotificationUseCase.execute(command);
        var acceptedAt = Instant.now();

        responseObserver.onNext(SendNotificationResponse.newBuilder()
            .setNotificationId(notification.notificationId())
            .setAcceptedAt(Timestamp.newBuilder()
                .setSeconds(acceptedAt.getEpochSecond())
                .setNanos(acceptedAt.getNano())
                .build())
            .build());
        responseObserver.onCompleted();
    }

    @Override
    public void streamNotifications(StreamNotificationsRequest request, StreamObserver<br.com.poc.grpc.notification.v1.Notification> responseObserver) {
        String userId = request.getUserId();
        log.info("StreamNotifications userId={} topics={}", userId, request.getTopicsList());

        var serverObserver = (ServerCallStreamObserver<br.com.poc.grpc.notification.v1.Notification>) responseObserver;

        Consumer<br.com.poc.grpc.notification.domain.model.Notification> subscriber = domainNotification -> {
            if (!userId.equals(domainNotification.userId())) return;
            if (!request.getTopicsList().isEmpty() && !request.getTopicsList().contains(domainNotification.topic())) return;
            if (domainNotification.priority().ordinal() < mapper.toDomainPriority(request.getMinPriority()).ordinal()) return;

            if (!serverObserver.isCancelled()) {
                serverObserver.onNext(mapper.toProto(domainNotification));
            }
        };

        publisher.subscribe(subscriber);

        serverObserver.setOnCancelHandler(() -> {
            publisher.unsubscribe(subscriber);
            log.info("StreamNotifications cancelled userId={}", userId);
        });
    }

    @Override
    public StreamObserver<NotificationAck> notificationChannel(StreamObserver<br.com.poc.grpc.notification.v1.NotificationStatus> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(NotificationAck ack) {
                log.info("NotificationChannel ack received notificationId={} userId={}", ack.getNotificationId(), ack.getUserId());
                var status = br.com.poc.grpc.notification.v1.NotificationStatus.newBuilder()
                    .setNotificationId(ack.getNotificationId())
                    .setState(br.com.poc.grpc.notification.v1.NotificationState.NOTIFICATION_STATE_ACKNOWLEDGED)
                    .build();
                responseObserver.onNext(status);
            }

            @Override
            public void onError(Throwable t) {
                log.warn("NotificationChannel error={}", t.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

}
