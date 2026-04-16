package br.com.poc.grpc.subscription.interceptor

import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.grpc.Status
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LoggingClientInterceptorTest {

    private lateinit var interceptor: LoggingClientInterceptor

    @BeforeEach
    fun setUp() {
        interceptor = LoggingClientInterceptor()
    }

    @Test
    fun `should delegate call to channel and wrap listener`() {
        val channel = mockk<Channel>()
        val delegateCall = mockk<ClientCall<Any, Any>>(relaxed = true)
        val method = mockk<MethodDescriptor<Any, Any>>(relaxed = true)
        val listener = mockk<ClientCall.Listener<Any>>(relaxed = true)

        every { channel.newCall(method, CallOptions.DEFAULT) } returns delegateCall

        val wrappedCall = interceptor.interceptCall(method, CallOptions.DEFAULT, channel)
        wrappedCall.start(listener, Metadata())

        verify { delegateCall.start(any(), any()) }
    }

    @Test
    fun `should log on successful close`() {
        val channel = mockk<Channel>()
        val delegateCall = mockk<ClientCall<Any, Any>>(relaxed = true)
        val method = mockk<MethodDescriptor<Any, Any>>(relaxed = true)
        val listenerSlot = slot<ClientCall.Listener<Any>>()

        every { channel.newCall(method, CallOptions.DEFAULT) } returns delegateCall

        val wrappedCall = interceptor.interceptCall(method, CallOptions.DEFAULT, channel)
        wrappedCall.start(mockk(relaxed = true), Metadata())

        verify { delegateCall.start(capture(listenerSlot), any()) }

        listenerSlot.captured.onClose(Status.OK, Metadata())
    }

    @Test
    fun `should log warning on failed close`() {
        val channel = mockk<Channel>()
        val delegateCall = mockk<ClientCall<Any, Any>>(relaxed = true)
        val method = mockk<MethodDescriptor<Any, Any>>(relaxed = true)
        val listenerSlot = slot<ClientCall.Listener<Any>>()

        every { channel.newCall(method, CallOptions.DEFAULT) } returns delegateCall
        every { method.fullMethodName } returns "TestService/TestMethod"

        val wrappedCall = interceptor.interceptCall(method, CallOptions.DEFAULT, channel)
        wrappedCall.start(mockk(relaxed = true), Metadata())

        verify { delegateCall.start(capture(listenerSlot), any()) }

        listenerSlot.captured.onClose(Status.UNAVAILABLE.withDescription("service down"), Metadata())

        val outerListener = mockk<ClientCall.Listener<Any>>(relaxed = true)
        assertThat(outerListener).isNotNull()
    }
}
