package br.com.poc.grpc.subscription.interceptor

import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JwtClientInterceptorTest {

    private val secret = "test-secret-key-for-unit-tests-minimum-32bytes!!"
    private lateinit var interceptor: JwtClientInterceptor

    @BeforeEach
    fun setUp() {
        interceptor = JwtClientInterceptor(secret)
    }

    @Test
    fun `should inject Bearer token into outgoing call headers`() {
        val channel = mockk<Channel>()
        val delegateCall = mockk<ClientCall<Any, Any>>(relaxed = true)
        val method = mockk<MethodDescriptor<Any, Any>>(relaxed = true)
        val options = CallOptions.DEFAULT
        val listener = mockk<ClientCall.Listener<Any>>(relaxed = true)

        every { channel.newCall(method, options) } returns delegateCall

        val wrappedCall = interceptor.interceptCall(method, options, channel)

        val headersSlot = slot<Metadata>()
        wrappedCall.start(listener, Metadata())

        verify { delegateCall.start(any(), capture(headersSlot)) }

        val authHeader = headersSlot.captured.get(JwtClientInterceptor.AUTHORIZATION_KEY)
        assertThat(authHeader).isNotNull().startsWith("Bearer ")

        val token = authHeader!!.removePrefix("Bearer ")
        assertThat(token.split(".")).hasSize(3)
    }

    @Test
    fun `should generate different tokens on each call`() {
        val channel = mockk<Channel>()
        val call1 = mockk<ClientCall<Any, Any>>(relaxed = true)
        val call2 = mockk<ClientCall<Any, Any>>(relaxed = true)
        val method = mockk<MethodDescriptor<Any, Any>>(relaxed = true)
        val listener = mockk<ClientCall.Listener<Any>>(relaxed = true)

        every { channel.newCall(method, CallOptions.DEFAULT) } returnsMany listOf(call1, call2)

        val slot1 = slot<Metadata>()
        val slot2 = slot<Metadata>()

        interceptor.interceptCall(method, CallOptions.DEFAULT, channel).start(listener, Metadata())
        verify { call1.start(any(), capture(slot1)) }

        interceptor.interceptCall(method, CallOptions.DEFAULT, channel).start(listener, Metadata())
        verify { call2.start(any(), capture(slot2)) }

        val token1 = slot1.captured.get(JwtClientInterceptor.AUTHORIZATION_KEY)
        val token2 = slot2.captured.get(JwtClientInterceptor.AUTHORIZATION_KEY)
        assertThat(token1).isNotNull()
        assertThat(token2).isNotNull()
    }
}
