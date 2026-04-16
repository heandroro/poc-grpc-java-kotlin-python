package br.com.poc.grpc.notification.v1;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * NotificationService handles dispatching, streaming and acknowledging notifications.
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.63.0)",
    comments = "Source: notification/v1/notification.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class NotificationServiceGrpc {

  private NotificationServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "notification.v1.NotificationService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<br.com.poc.grpc.notification.v1.SendNotificationRequest,
      br.com.poc.grpc.notification.v1.SendNotificationResponse> getSendNotificationMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SendNotification",
      requestType = br.com.poc.grpc.notification.v1.SendNotificationRequest.class,
      responseType = br.com.poc.grpc.notification.v1.SendNotificationResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<br.com.poc.grpc.notification.v1.SendNotificationRequest,
      br.com.poc.grpc.notification.v1.SendNotificationResponse> getSendNotificationMethod() {
    io.grpc.MethodDescriptor<br.com.poc.grpc.notification.v1.SendNotificationRequest, br.com.poc.grpc.notification.v1.SendNotificationResponse> getSendNotificationMethod;
    if ((getSendNotificationMethod = NotificationServiceGrpc.getSendNotificationMethod) == null) {
      synchronized (NotificationServiceGrpc.class) {
        if ((getSendNotificationMethod = NotificationServiceGrpc.getSendNotificationMethod) == null) {
          NotificationServiceGrpc.getSendNotificationMethod = getSendNotificationMethod =
              io.grpc.MethodDescriptor.<br.com.poc.grpc.notification.v1.SendNotificationRequest, br.com.poc.grpc.notification.v1.SendNotificationResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SendNotification"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.com.poc.grpc.notification.v1.SendNotificationRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.com.poc.grpc.notification.v1.SendNotificationResponse.getDefaultInstance()))
              .setSchemaDescriptor(new NotificationServiceMethodDescriptorSupplier("SendNotification"))
              .build();
        }
      }
    }
    return getSendNotificationMethod;
  }

  private static volatile io.grpc.MethodDescriptor<br.com.poc.grpc.notification.v1.StreamNotificationsRequest,
      br.com.poc.grpc.notification.v1.Notification> getStreamNotificationsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "StreamNotifications",
      requestType = br.com.poc.grpc.notification.v1.StreamNotificationsRequest.class,
      responseType = br.com.poc.grpc.notification.v1.Notification.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<br.com.poc.grpc.notification.v1.StreamNotificationsRequest,
      br.com.poc.grpc.notification.v1.Notification> getStreamNotificationsMethod() {
    io.grpc.MethodDescriptor<br.com.poc.grpc.notification.v1.StreamNotificationsRequest, br.com.poc.grpc.notification.v1.Notification> getStreamNotificationsMethod;
    if ((getStreamNotificationsMethod = NotificationServiceGrpc.getStreamNotificationsMethod) == null) {
      synchronized (NotificationServiceGrpc.class) {
        if ((getStreamNotificationsMethod = NotificationServiceGrpc.getStreamNotificationsMethod) == null) {
          NotificationServiceGrpc.getStreamNotificationsMethod = getStreamNotificationsMethod =
              io.grpc.MethodDescriptor.<br.com.poc.grpc.notification.v1.StreamNotificationsRequest, br.com.poc.grpc.notification.v1.Notification>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "StreamNotifications"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.com.poc.grpc.notification.v1.StreamNotificationsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.com.poc.grpc.notification.v1.Notification.getDefaultInstance()))
              .setSchemaDescriptor(new NotificationServiceMethodDescriptorSupplier("StreamNotifications"))
              .build();
        }
      }
    }
    return getStreamNotificationsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<br.com.poc.grpc.notification.v1.NotificationAck,
      br.com.poc.grpc.notification.v1.NotificationStatus> getNotificationChannelMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "NotificationChannel",
      requestType = br.com.poc.grpc.notification.v1.NotificationAck.class,
      responseType = br.com.poc.grpc.notification.v1.NotificationStatus.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<br.com.poc.grpc.notification.v1.NotificationAck,
      br.com.poc.grpc.notification.v1.NotificationStatus> getNotificationChannelMethod() {
    io.grpc.MethodDescriptor<br.com.poc.grpc.notification.v1.NotificationAck, br.com.poc.grpc.notification.v1.NotificationStatus> getNotificationChannelMethod;
    if ((getNotificationChannelMethod = NotificationServiceGrpc.getNotificationChannelMethod) == null) {
      synchronized (NotificationServiceGrpc.class) {
        if ((getNotificationChannelMethod = NotificationServiceGrpc.getNotificationChannelMethod) == null) {
          NotificationServiceGrpc.getNotificationChannelMethod = getNotificationChannelMethod =
              io.grpc.MethodDescriptor.<br.com.poc.grpc.notification.v1.NotificationAck, br.com.poc.grpc.notification.v1.NotificationStatus>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "NotificationChannel"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.com.poc.grpc.notification.v1.NotificationAck.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.com.poc.grpc.notification.v1.NotificationStatus.getDefaultInstance()))
              .setSchemaDescriptor(new NotificationServiceMethodDescriptorSupplier("NotificationChannel"))
              .build();
        }
      }
    }
    return getNotificationChannelMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static NotificationServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<NotificationServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<NotificationServiceStub>() {
        @java.lang.Override
        public NotificationServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new NotificationServiceStub(channel, callOptions);
        }
      };
    return NotificationServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static NotificationServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<NotificationServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<NotificationServiceBlockingStub>() {
        @java.lang.Override
        public NotificationServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new NotificationServiceBlockingStub(channel, callOptions);
        }
      };
    return NotificationServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static NotificationServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<NotificationServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<NotificationServiceFutureStub>() {
        @java.lang.Override
        public NotificationServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new NotificationServiceFutureStub(channel, callOptions);
        }
      };
    return NotificationServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * NotificationService handles dispatching, streaming and acknowledging notifications.
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * SendNotification dispatches a single notification (unary).
     * </pre>
     */
    default void sendNotification(br.com.poc.grpc.notification.v1.SendNotificationRequest request,
        io.grpc.stub.StreamObserver<br.com.poc.grpc.notification.v1.SendNotificationResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSendNotificationMethod(), responseObserver);
    }

    /**
     * <pre>
     * StreamNotifications opens a server-streaming channel to receive live notifications.
     * </pre>
     */
    default void streamNotifications(br.com.poc.grpc.notification.v1.StreamNotificationsRequest request,
        io.grpc.stub.StreamObserver<br.com.poc.grpc.notification.v1.Notification> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getStreamNotificationsMethod(), responseObserver);
    }

    /**
     * <pre>
     * NotificationChannel is a bidirectional stream for real-time ack/status updates.
     * </pre>
     */
    default io.grpc.stub.StreamObserver<br.com.poc.grpc.notification.v1.NotificationAck> notificationChannel(
        io.grpc.stub.StreamObserver<br.com.poc.grpc.notification.v1.NotificationStatus> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getNotificationChannelMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service NotificationService.
   * <pre>
   * NotificationService handles dispatching, streaming and acknowledging notifications.
   * </pre>
   */
  public static abstract class NotificationServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return NotificationServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service NotificationService.
   * <pre>
   * NotificationService handles dispatching, streaming and acknowledging notifications.
   * </pre>
   */
  public static final class NotificationServiceStub
      extends io.grpc.stub.AbstractAsyncStub<NotificationServiceStub> {
    private NotificationServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NotificationServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new NotificationServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * SendNotification dispatches a single notification (unary).
     * </pre>
     */
    public void sendNotification(br.com.poc.grpc.notification.v1.SendNotificationRequest request,
        io.grpc.stub.StreamObserver<br.com.poc.grpc.notification.v1.SendNotificationResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSendNotificationMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * StreamNotifications opens a server-streaming channel to receive live notifications.
     * </pre>
     */
    public void streamNotifications(br.com.poc.grpc.notification.v1.StreamNotificationsRequest request,
        io.grpc.stub.StreamObserver<br.com.poc.grpc.notification.v1.Notification> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getStreamNotificationsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * NotificationChannel is a bidirectional stream for real-time ack/status updates.
     * </pre>
     */
    public io.grpc.stub.StreamObserver<br.com.poc.grpc.notification.v1.NotificationAck> notificationChannel(
        io.grpc.stub.StreamObserver<br.com.poc.grpc.notification.v1.NotificationStatus> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncBidiStreamingCall(
          getChannel().newCall(getNotificationChannelMethod(), getCallOptions()), responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service NotificationService.
   * <pre>
   * NotificationService handles dispatching, streaming and acknowledging notifications.
   * </pre>
   */
  public static final class NotificationServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<NotificationServiceBlockingStub> {
    private NotificationServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NotificationServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new NotificationServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * SendNotification dispatches a single notification (unary).
     * </pre>
     */
    public br.com.poc.grpc.notification.v1.SendNotificationResponse sendNotification(br.com.poc.grpc.notification.v1.SendNotificationRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSendNotificationMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * StreamNotifications opens a server-streaming channel to receive live notifications.
     * </pre>
     */
    public java.util.Iterator<br.com.poc.grpc.notification.v1.Notification> streamNotifications(
        br.com.poc.grpc.notification.v1.StreamNotificationsRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getStreamNotificationsMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service NotificationService.
   * <pre>
   * NotificationService handles dispatching, streaming and acknowledging notifications.
   * </pre>
   */
  public static final class NotificationServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<NotificationServiceFutureStub> {
    private NotificationServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NotificationServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new NotificationServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * SendNotification dispatches a single notification (unary).
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<br.com.poc.grpc.notification.v1.SendNotificationResponse> sendNotification(
        br.com.poc.grpc.notification.v1.SendNotificationRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSendNotificationMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SEND_NOTIFICATION = 0;
  private static final int METHODID_STREAM_NOTIFICATIONS = 1;
  private static final int METHODID_NOTIFICATION_CHANNEL = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SEND_NOTIFICATION:
          serviceImpl.sendNotification((br.com.poc.grpc.notification.v1.SendNotificationRequest) request,
              (io.grpc.stub.StreamObserver<br.com.poc.grpc.notification.v1.SendNotificationResponse>) responseObserver);
          break;
        case METHODID_STREAM_NOTIFICATIONS:
          serviceImpl.streamNotifications((br.com.poc.grpc.notification.v1.StreamNotificationsRequest) request,
              (io.grpc.stub.StreamObserver<br.com.poc.grpc.notification.v1.Notification>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_NOTIFICATION_CHANNEL:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.notificationChannel(
              (io.grpc.stub.StreamObserver<br.com.poc.grpc.notification.v1.NotificationStatus>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getSendNotificationMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              br.com.poc.grpc.notification.v1.SendNotificationRequest,
              br.com.poc.grpc.notification.v1.SendNotificationResponse>(
                service, METHODID_SEND_NOTIFICATION)))
        .addMethod(
          getStreamNotificationsMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              br.com.poc.grpc.notification.v1.StreamNotificationsRequest,
              br.com.poc.grpc.notification.v1.Notification>(
                service, METHODID_STREAM_NOTIFICATIONS)))
        .addMethod(
          getNotificationChannelMethod(),
          io.grpc.stub.ServerCalls.asyncBidiStreamingCall(
            new MethodHandlers<
              br.com.poc.grpc.notification.v1.NotificationAck,
              br.com.poc.grpc.notification.v1.NotificationStatus>(
                service, METHODID_NOTIFICATION_CHANNEL)))
        .build();
  }

  private static abstract class NotificationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    NotificationServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return br.com.poc.grpc.notification.v1.NotificationProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("NotificationService");
    }
  }

  private static final class NotificationServiceFileDescriptorSupplier
      extends NotificationServiceBaseDescriptorSupplier {
    NotificationServiceFileDescriptorSupplier() {}
  }

  private static final class NotificationServiceMethodDescriptorSupplier
      extends NotificationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    NotificationServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (NotificationServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new NotificationServiceFileDescriptorSupplier())
              .addMethod(getSendNotificationMethod())
              .addMethod(getStreamNotificationsMethod())
              .addMethod(getNotificationChannelMethod())
              .build();
        }
      }
    }
    return result;
  }
}
