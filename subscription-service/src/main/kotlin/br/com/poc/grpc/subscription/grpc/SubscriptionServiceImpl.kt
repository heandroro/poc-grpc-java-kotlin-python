package br.com.poc.grpc.subscription.grpc

import br.com.poc.grpc.notification.v1.NotificationServiceGrpcKt
import br.com.poc.grpc.notification.v1.sendNotificationRequest
import br.com.poc.grpc.subscription.v1.ListSubscriptionsRequest
import br.com.poc.grpc.subscription.v1.PublishToSubscribersRequest
import br.com.poc.grpc.subscription.v1.PublishToSubscribersResponse
import br.com.poc.grpc.subscription.v1.SubscribeRequest
import br.com.poc.grpc.subscription.v1.SubscribeResponse
import br.com.poc.grpc.subscription.v1.Subscription
import br.com.poc.grpc.subscription.v1.SubscriptionServiceGrpcKt
import br.com.poc.grpc.subscription.v1.UnsubscribeRequest
import br.com.poc.grpc.subscription.v1.UnsubscribeResponse
import br.com.poc.grpc.subscription.v1.subscribeResponse
import br.com.poc.grpc.subscription.v1.subscription
import br.com.poc.grpc.subscription.v1.unsubscribeResponse
import com.google.protobuf.Timestamp
import io.grpc.Status
import io.grpc.StatusException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Best Practice: Demonstrates deadline propagation, retry-safe operations, and proper error codes
 * using Kotlin coroutines with gRPC-Kotlin.
 */
class SubscriptionServiceImpl(
    private val notificationStub: NotificationServiceGrpcKt.NotificationServiceCoroutineStub,
) : SubscriptionServiceGrpcKt.SubscriptionServiceCoroutineImplBase() {

    private val log = LoggerFactory.getLogger(SubscriptionServiceImpl::class.java)

    private data class SubscriptionRecord(
        val subscriptionId: String,
        val userId: String,
        val topic: String,
        val minPriority: Int,
        val subscribedAt: Instant,
        val active: Boolean,
    )

    private val store = ConcurrentHashMap<String, SubscriptionRecord>()

    override suspend fun subscribe(request: SubscribeRequest): SubscribeResponse {
        if (request.userId.isBlank() || request.topic.isBlank()) {
            throw StatusException(
                Status.INVALID_ARGUMENT.withDescription("user_id and topic must not be blank")
            )
        }

        val existing = store.values.find { it.userId == request.userId && it.topic == request.topic && it.active }
        if (existing != null) {
            log.info("Subscription already active subscriptionId={} userId={} topic={}", existing.subscriptionId, request.userId, request.topic)
            val ts = toTimestamp(existing.subscribedAt)
            return subscribeResponse {
                subscriptionId = existing.subscriptionId
                subscribedAt = ts
            }
        }

        val id = UUID.randomUUID().toString()
        val now = Instant.now()
        store[id] = SubscriptionRecord(
            subscriptionId = id,
            userId = request.userId,
            topic = request.topic,
            minPriority = request.minPriority.number,
            subscribedAt = now,
            active = true,
        )
        log.info("Subscribed subscriptionId={} userId={} topic={}", id, request.userId, request.topic)
        return subscribeResponse {
            subscriptionId = id
            subscribedAt = toTimestamp(now)
        }
    }

    override suspend fun unsubscribe(request: UnsubscribeRequest): UnsubscribeResponse {
        val record = store.values.find { it.userId == request.userId && it.topic == request.topic && it.active }
            ?: throw StatusException(Status.NOT_FOUND.withDescription("No active subscription found for userId=${request.userId} topic=${request.topic}"))

        store[record.subscriptionId] = record.copy(active = false)
        log.info("Unsubscribed subscriptionId={} userId={} topic={}", record.subscriptionId, request.userId, request.topic)
        return unsubscribeResponse { success = true }
    }

    override fun listSubscriptions(request: ListSubscriptionsRequest): Flow<Subscription> = flow {
        val subs = store.values
            .filter { it.userId == request.userId }
            .filter { if (request.activeOnly) it.active else true }

        for (s in subs) {
            emit(subscription {
                subscriptionId = s.subscriptionId
                userId = s.userId
                topic = s.topic
                subscribedAt = toTimestamp(s.subscribedAt)
                active = s.active
            })
        }
        log.info("ListSubscriptions userId={} count={}", request.userId, subs.size)
    }

    override suspend fun publishToSubscribers(request: PublishToSubscribersRequest): PublishToSubscribersResponse {
        val subscribers = store.values.filter { it.topic == request.topic && it.active }
        if (subscribers.isEmpty()) {
            log.warn("No subscribers for topic={}", request.topic)
            return PublishToSubscribersResponse.newBuilder().setNotificationsSent(0).build()
        }

        var sent = 0
        val failed = mutableListOf<String>()

        for (sub in subscribers) {
            try {
                /**
                 * Best Practice: withTimeout enforces a deadline on each downstream call.
                 * This prevents one slow/failing dependency from blocking the whole loop.
                 */
                withTimeout(5_000) {
                    notificationStub.sendNotification(sendNotificationRequest {
                        userId = sub.userId
                        topic = request.topic
                        title = request.title
                        body = request.body
                        priority = request.priority
                        putAllMetadata(request.metadataMap)
                    })
                }
                sent++
                log.info("Notification sent userId={} topic={}", sub.userId, request.topic)
            } catch (e: Exception) {
                log.warn("Failed to send notification userId={} topic={} error={}", sub.userId, request.topic, e.message)
                failed.add(sub.userId)
            }
        }

        return PublishToSubscribersResponse.newBuilder()
            .setNotificationsSent(sent)
            .addAllFailedUserIds(failed)
            .build()
    }

    private fun toTimestamp(instant: Instant): Timestamp =
        Timestamp.newBuilder().setSeconds(instant.epochSecond).setNanos(instant.nano).build()
}
