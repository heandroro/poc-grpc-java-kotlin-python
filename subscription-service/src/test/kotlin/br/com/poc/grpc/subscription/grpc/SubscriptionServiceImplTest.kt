package br.com.poc.grpc.subscription.grpc

import br.com.poc.grpc.notification.v1.NotificationServiceGrpcKt
import br.com.poc.grpc.notification.v1.SendNotificationResponse
import br.com.poc.grpc.subscription.v1.ListSubscriptionsRequest
import br.com.poc.grpc.subscription.v1.NotificationPriority
import br.com.poc.grpc.subscription.v1.PublishToSubscribersRequest
import br.com.poc.grpc.subscription.v1.SubscribeRequest
import br.com.poc.grpc.subscription.v1.UnsubscribeRequest
import io.grpc.Status
import io.grpc.StatusException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SubscriptionServiceImplTest {

    private lateinit var stub: NotificationServiceGrpcKt.NotificationServiceCoroutineStub
    private lateinit var service: SubscriptionServiceImpl

    @BeforeEach
    fun setUp() {
        stub = mockk()
        service = SubscriptionServiceImpl(stub)
    }

    @Test
    fun `should subscribe new user to topic`() = runTest {
        val request = SubscribeRequest.newBuilder()
            .setUserId("user-001")
            .setTopic("promos")
            .setMinPriority(NotificationPriority.NOTIFICATION_PRIORITY_NORMAL)
            .build()

        val response = service.subscribe(request)

        assertThat(response.subscriptionId).isNotBlank()
        assertThat(response.hasSubscribedAt()).isTrue()
    }

    @Test
    fun `should return existing subscription when already subscribed`() = runTest {
        val request = SubscribeRequest.newBuilder()
            .setUserId("user-001")
            .setTopic("promos")
            .build()

        val first = service.subscribe(request)
        val second = service.subscribe(request)

        assertThat(second.subscriptionId).isEqualTo(first.subscriptionId)
    }

    @Test
    fun `should throw INVALID_ARGUMENT when user_id is blank`() = runTest {
        val request = SubscribeRequest.newBuilder().setUserId("").setTopic("promos").build()

        assertThatThrownBy { runTest { service.subscribe(request) } }
            .hasCauseInstanceOf(StatusException::class.java)
            .satisfies { t ->
                val cause = t.cause as StatusException
                assertThat(cause.status.code).isEqualTo(Status.INVALID_ARGUMENT.code)
            }
    }

    @Test
    fun `should throw INVALID_ARGUMENT when topic is blank`() = runTest {
        val request = SubscribeRequest.newBuilder().setUserId("user-001").setTopic("").build()

        assertThatThrownBy { runTest { service.subscribe(request) } }
            .hasCauseInstanceOf(StatusException::class.java)
    }

    @Test
    fun `should unsubscribe existing subscription`() = runTest {
        val subscribeReq = SubscribeRequest.newBuilder().setUserId("user-001").setTopic("promos").build()
        service.subscribe(subscribeReq)

        val unsubscribeReq = UnsubscribeRequest.newBuilder().setUserId("user-001").setTopic("promos").build()
        val response = service.unsubscribe(unsubscribeReq)

        assertThat(response.success).isTrue()
    }

    @Test
    fun `should throw NOT_FOUND when unsubscribing nonexistent subscription`() = runTest {
        val request = UnsubscribeRequest.newBuilder().setUserId("ghost").setTopic("promos").build()

        assertThatThrownBy { runTest { service.unsubscribe(request) } }
            .hasCauseInstanceOf(StatusException::class.java)
            .satisfies { t ->
                val cause = t.cause as StatusException
                assertThat(cause.status.code).isEqualTo(Status.NOT_FOUND.code)
            }
    }

    @Test
    fun `should list only active subscriptions when activeOnly is true`() = runTest {
        val sub = SubscribeRequest.newBuilder().setUserId("user-001").setTopic("promos").build()
        service.subscribe(sub)
        val sub2 = SubscribeRequest.newBuilder().setUserId("user-001").setTopic("alerts").build()
        service.subscribe(sub2)
        service.unsubscribe(UnsubscribeRequest.newBuilder().setUserId("user-001").setTopic("alerts").build())

        val request = ListSubscriptionsRequest.newBuilder().setUserId("user-001").setActiveOnly(true).build()
        val results = service.listSubscriptions(request).toList()

        assertThat(results).hasSize(1)
        assertThat(results[0].topic).isEqualTo("promos")
    }

    @Test
    fun `should list all subscriptions when activeOnly is false`() = runTest {
        val sub = SubscribeRequest.newBuilder().setUserId("user-001").setTopic("promos").build()
        service.subscribe(sub)
        val sub2 = SubscribeRequest.newBuilder().setUserId("user-001").setTopic("alerts").build()
        service.subscribe(sub2)
        service.unsubscribe(UnsubscribeRequest.newBuilder().setUserId("user-001").setTopic("alerts").build())

        val request = ListSubscriptionsRequest.newBuilder().setUserId("user-001").setActiveOnly(false).build()
        val results = service.listSubscriptions(request).toList()

        assertThat(results).hasSize(2)
    }

    @Test
    fun `should publish to all active subscribers successfully`() = runTest {
        val sub = SubscribeRequest.newBuilder().setUserId("user-001").setTopic("promos").build()
        service.subscribe(sub)

        coEvery { stub.sendNotification(any()) } returns SendNotificationResponse.getDefaultInstance()

        val request = PublishToSubscribersRequest.newBuilder()
            .setTopic("promos")
            .setTitle("Flash Sale")
            .setBody("50% off")
            .build()

        val response = service.publishToSubscribers(request)

        assertThat(response.notificationsSent).isEqualTo(1)
        assertThat(response.failedUserIdsList).isEmpty()
    }

    @Test
    fun `should return zero notifications sent when no subscribers for topic`() = runTest {
        val request = PublishToSubscribersRequest.newBuilder().setTopic("empty-topic").build()

        val response = service.publishToSubscribers(request)

        assertThat(response.notificationsSent).isEqualTo(0)
    }

    @Test
    fun `should record failed user when downstream call throws`() = runTest {
        val sub = SubscribeRequest.newBuilder().setUserId("user-001").setTopic("promos").build()
        service.subscribe(sub)

        coEvery { stub.sendNotification(any()) } throws StatusException(Status.UNAVAILABLE)

        val request = PublishToSubscribersRequest.newBuilder().setTopic("promos").setTitle("T").setBody("B").build()
        val response = service.publishToSubscribers(request)

        assertThat(response.notificationsSent).isEqualTo(0)
        assertThat(response.failedUserIdsList).contains("user-001")
    }
}
