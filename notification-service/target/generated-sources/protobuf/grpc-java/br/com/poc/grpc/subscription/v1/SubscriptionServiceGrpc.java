package br.com.poc.grpc.subscription.v1;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * SubscriptionService manages user topic subscriptions.
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.63.0)",
    comments = "Source: subscription/v1/subscription.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class SubscriptionServiceGrpc {

  private SubscriptionServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "subscription.v1.SubscriptionService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<br.com.poc.grpc.subscription.v1.SubscribeRequest,
      br.com.poc.grpc.subscription.v1.SubscribeResponse> getSubscribeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Subscribe",
      requestType = br.com.poc.grpc.subscription.v1.SubscribeRequest.class,
      responseType = br.com.poc.grpc.subscription.v1.SubscribeResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<br.com.poc.grpc.subscription.v1.SubscribeRequest,
      br.com.poc.grpc.subscription.v1.SubscribeResponse> getSubscribeMethod() {
    io.grpc.MethodDescriptor<br.com.poc.grpc.subscription.v1.SubscribeRequest, br.com.poc.grpc.subscription.v1.SubscribeResponse> getSubscribeMethod;
    if ((getSubscribeMethod = SubscriptionServiceGrpc.getSubscribeMethod) == null) {
      synchronized (SubscriptionServiceGrpc.class) {
        if ((getSubscribeMethod = SubscriptionServiceGrpc.getSubscribeMethod) == null) {
          SubscriptionServiceGrpc.getSubscribeMethod = getSubscribeMethod =
              io.grpc.MethodDescriptor.<br.com.poc.grpc.subscription.v1.SubscribeRequest, br.com.poc.grpc.subscription.v1.SubscribeResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Subscribe"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.com.poc.grpc.subscription.v1.SubscribeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.com.poc.grpc.subscription.v1.SubscribeResponse.getDefaultInstance()))
              .setSchemaDescriptor(new SubscriptionServiceMethodDescriptorSupplier("Subscribe"))
              .build();
        }
      }
    }
    return getSubscribeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<br.com.poc.grpc.subscription.v1.UnsubscribeRequest,
      br.com.poc.grpc.subscription.v1.UnsubscribeResponse> getUnsubscribeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Unsubscribe",
      requestType = br.com.poc.grpc.subscription.v1.UnsubscribeRequest.class,
      responseType = br.com.poc.grpc.subscription.v1.UnsubscribeResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<br.com.poc.grpc.subscription.v1.UnsubscribeRequest,
      br.com.poc.grpc.subscription.v1.UnsubscribeResponse> getUnsubscribeMethod() {
    io.grpc.MethodDescriptor<br.com.poc.grpc.subscription.v1.UnsubscribeRequest, br.com.poc.grpc.subscription.v1.UnsubscribeResponse> getUnsubscribeMethod;
    if ((getUnsubscribeMethod = SubscriptionServiceGrpc.getUnsubscribeMethod) == null) {
      synchronized (SubscriptionServiceGrpc.class) {
        if ((getUnsubscribeMethod = SubscriptionServiceGrpc.getUnsubscribeMethod) == null) {
          SubscriptionServiceGrpc.getUnsubscribeMethod = getUnsubscribeMethod =
              io.grpc.MethodDescriptor.<br.com.poc.grpc.subscription.v1.UnsubscribeRequest, br.com.poc.grpc.subscription.v1.UnsubscribeResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Unsubscribe"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.com.poc.grpc.subscription.v1.UnsubscribeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.com.poc.grpc.subscription.v1.UnsubscribeResponse.getDefaultInstance()))
              .setSchemaDescriptor(new SubscriptionServiceMethodDescriptorSupplier("Unsubscribe"))
              .build();
        }
      }
    }
    return getUnsubscribeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<br.com.poc.grpc.subscription.v1.ListSubscriptionsRequest,
      br.com.poc.grpc.subscription.v1.Subscription> getListSubscriptionsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListSubscriptions",
      requestType = br.com.poc.grpc.subscription.v1.ListSubscriptionsRequest.class,
      responseType = br.com.poc.grpc.subscription.v1.Subscription.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<br.com.poc.grpc.subscription.v1.ListSubscriptionsRequest,
      br.com.poc.grpc.subscription.v1.Subscription> getListSubscriptionsMethod() {
    io.grpc.MethodDescriptor<br.com.poc.grpc.subscription.v1.ListSubscriptionsRequest, br.com.poc.grpc.subscription.v1.Subscription> getListSubscriptionsMethod;
    if ((getListSubscriptionsMethod = SubscriptionServiceGrpc.getListSubscriptionsMethod) == null) {
      synchronized (SubscriptionServiceGrpc.class) {
        if ((getListSubscriptionsMethod = SubscriptionServiceGrpc.getListSubscriptionsMethod) == null) {
          SubscriptionServiceGrpc.getListSubscriptionsMethod = getListSubscriptionsMethod =
              io.grpc.MethodDescriptor.<br.com.poc.grpc.subscription.v1.ListSubscriptionsRequest, br.com.poc.grpc.subscription.v1.Subscription>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ListSubscriptions"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.com.poc.grpc.subscription.v1.ListSubscriptionsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.com.poc.grpc.subscription.v1.Subscription.getDefaultInstance()))
              .setSchemaDescriptor(new SubscriptionServiceMethodDescriptorSupplier("ListSubscriptions"))
              .build();
        }
      }
    }
    return getListSubscriptionsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<br.com.poc.grpc.subscription.v1.PublishToSubscribersRequest,
      br.com.poc.grpc.subscription.v1.PublishToSubscribersResponse> getPublishToSubscribersMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "PublishToSubscribers",
      requestType = br.com.poc.grpc.subscription.v1.PublishToSubscribersRequest.class,
      responseType = br.com.poc.grpc.subscription.v1.PublishToSubscribersResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<br.com.poc.grpc.subscription.v1.PublishToSubscribersRequest,
      br.com.poc.grpc.subscription.v1.PublishToSubscribersResponse> getPublishToSubscribersMethod() {
    io.grpc.MethodDescriptor<br.com.poc.grpc.subscription.v1.PublishToSubscribersRequest, br.com.poc.grpc.subscription.v1.PublishToSubscribersResponse> getPublishToSubscribersMethod;
    if ((getPublishToSubscribersMethod = SubscriptionServiceGrpc.getPublishToSubscribersMethod) == null) {
      synchronized (SubscriptionServiceGrpc.class) {
        if ((getPublishToSubscribersMethod = SubscriptionServiceGrpc.getPublishToSubscribersMethod) == null) {
          SubscriptionServiceGrpc.getPublishToSubscribersMethod = getPublishToSubscribersMethod =
              io.grpc.MethodDescriptor.<br.com.poc.grpc.subscription.v1.PublishToSubscribersRequest, br.com.poc.grpc.subscription.v1.PublishToSubscribersResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "PublishToSubscribers"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.com.poc.grpc.subscription.v1.PublishToSubscribersRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.com.poc.grpc.subscription.v1.PublishToSubscribersResponse.getDefaultInstance()))
              .setSchemaDescriptor(new SubscriptionServiceMethodDescriptorSupplier("PublishToSubscribers"))
              .build();
        }
      }
    }
    return getPublishToSubscribersMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static SubscriptionServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SubscriptionServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SubscriptionServiceStub>() {
        @java.lang.Override
        public SubscriptionServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SubscriptionServiceStub(channel, callOptions);
        }
      };
    return SubscriptionServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static SubscriptionServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SubscriptionServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SubscriptionServiceBlockingStub>() {
        @java.lang.Override
        public SubscriptionServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SubscriptionServiceBlockingStub(channel, callOptions);
        }
      };
    return SubscriptionServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static SubscriptionServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SubscriptionServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SubscriptionServiceFutureStub>() {
        @java.lang.Override
        public SubscriptionServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SubscriptionServiceFutureStub(channel, callOptions);
        }
      };
    return SubscriptionServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * SubscriptionService manages user topic subscriptions.
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * Subscribe registers a user to receive notifications for a topic (unary).
     * </pre>
     */
    default void subscribe(br.com.poc.grpc.subscription.v1.SubscribeRequest request,
        io.grpc.stub.StreamObserver<br.com.poc.grpc.subscription.v1.SubscribeResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSubscribeMethod(), responseObserver);
    }

    /**
     * <pre>
     * Unsubscribe removes a user subscription (unary).
     * </pre>
     */
    default void unsubscribe(br.com.poc.grpc.subscription.v1.UnsubscribeRequest request,
        io.grpc.stub.StreamObserver<br.com.poc.grpc.subscription.v1.UnsubscribeResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUnsubscribeMethod(), responseObserver);
    }

    /**
     * <pre>
     * ListSubscriptions streams all active subscriptions for a user (server streaming).
     * </pre>
     */
    default void listSubscriptions(br.com.poc.grpc.subscription.v1.ListSubscriptionsRequest request,
        io.grpc.stub.StreamObserver<br.com.poc.grpc.subscription.v1.Subscription> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getListSubscriptionsMethod(), responseObserver);
    }

    /**
     * <pre>
     * PublishToSubscribers notifies all subscribers of a topic (unary → calls notification-service).
     * </pre>
     */
    default void publishToSubscribers(br.com.poc.grpc.subscription.v1.PublishToSubscribersRequest request,
        io.grpc.stub.StreamObserver<br.com.poc.grpc.subscription.v1.PublishToSubscribersResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getPublishToSubscribersMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service SubscriptionService.
   * <pre>
   * SubscriptionService manages user topic subscriptions.
   * </pre>
   */
  public static abstract class SubscriptionServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return SubscriptionServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service SubscriptionService.
   * <pre>
   * SubscriptionService manages user topic subscriptions.
   * </pre>
   */
  public static final class SubscriptionServiceStub
      extends io.grpc.stub.AbstractAsyncStub<SubscriptionServiceStub> {
    private SubscriptionServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SubscriptionServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SubscriptionServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Subscribe registers a user to receive notifications for a topic (unary).
     * </pre>
     */
    public void subscribe(br.com.poc.grpc.subscription.v1.SubscribeRequest request,
        io.grpc.stub.StreamObserver<br.com.poc.grpc.subscription.v1.SubscribeResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSubscribeMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Unsubscribe removes a user subscription (unary).
     * </pre>
     */
    public void unsubscribe(br.com.poc.grpc.subscription.v1.UnsubscribeRequest request,
        io.grpc.stub.StreamObserver<br.com.poc.grpc.subscription.v1.UnsubscribeResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUnsubscribeMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * ListSubscriptions streams all active subscriptions for a user (server streaming).
     * </pre>
     */
    public void listSubscriptions(br.com.poc.grpc.subscription.v1.ListSubscriptionsRequest request,
        io.grpc.stub.StreamObserver<br.com.poc.grpc.subscription.v1.Subscription> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getListSubscriptionsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * PublishToSubscribers notifies all subscribers of a topic (unary → calls notification-service).
     * </pre>
     */
    public void publishToSubscribers(br.com.poc.grpc.subscription.v1.PublishToSubscribersRequest request,
        io.grpc.stub.StreamObserver<br.com.poc.grpc.subscription.v1.PublishToSubscribersResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getPublishToSubscribersMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service SubscriptionService.
   * <pre>
   * SubscriptionService manages user topic subscriptions.
   * </pre>
   */
  public static final class SubscriptionServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<SubscriptionServiceBlockingStub> {
    private SubscriptionServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SubscriptionServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SubscriptionServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Subscribe registers a user to receive notifications for a topic (unary).
     * </pre>
     */
    public br.com.poc.grpc.subscription.v1.SubscribeResponse subscribe(br.com.poc.grpc.subscription.v1.SubscribeRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSubscribeMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Unsubscribe removes a user subscription (unary).
     * </pre>
     */
    public br.com.poc.grpc.subscription.v1.UnsubscribeResponse unsubscribe(br.com.poc.grpc.subscription.v1.UnsubscribeRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUnsubscribeMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * ListSubscriptions streams all active subscriptions for a user (server streaming).
     * </pre>
     */
    public java.util.Iterator<br.com.poc.grpc.subscription.v1.Subscription> listSubscriptions(
        br.com.poc.grpc.subscription.v1.ListSubscriptionsRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getListSubscriptionsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * PublishToSubscribers notifies all subscribers of a topic (unary → calls notification-service).
     * </pre>
     */
    public br.com.poc.grpc.subscription.v1.PublishToSubscribersResponse publishToSubscribers(br.com.poc.grpc.subscription.v1.PublishToSubscribersRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getPublishToSubscribersMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service SubscriptionService.
   * <pre>
   * SubscriptionService manages user topic subscriptions.
   * </pre>
   */
  public static final class SubscriptionServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<SubscriptionServiceFutureStub> {
    private SubscriptionServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SubscriptionServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SubscriptionServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Subscribe registers a user to receive notifications for a topic (unary).
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<br.com.poc.grpc.subscription.v1.SubscribeResponse> subscribe(
        br.com.poc.grpc.subscription.v1.SubscribeRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSubscribeMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Unsubscribe removes a user subscription (unary).
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<br.com.poc.grpc.subscription.v1.UnsubscribeResponse> unsubscribe(
        br.com.poc.grpc.subscription.v1.UnsubscribeRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUnsubscribeMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * PublishToSubscribers notifies all subscribers of a topic (unary → calls notification-service).
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<br.com.poc.grpc.subscription.v1.PublishToSubscribersResponse> publishToSubscribers(
        br.com.poc.grpc.subscription.v1.PublishToSubscribersRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getPublishToSubscribersMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SUBSCRIBE = 0;
  private static final int METHODID_UNSUBSCRIBE = 1;
  private static final int METHODID_LIST_SUBSCRIPTIONS = 2;
  private static final int METHODID_PUBLISH_TO_SUBSCRIBERS = 3;

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
        case METHODID_SUBSCRIBE:
          serviceImpl.subscribe((br.com.poc.grpc.subscription.v1.SubscribeRequest) request,
              (io.grpc.stub.StreamObserver<br.com.poc.grpc.subscription.v1.SubscribeResponse>) responseObserver);
          break;
        case METHODID_UNSUBSCRIBE:
          serviceImpl.unsubscribe((br.com.poc.grpc.subscription.v1.UnsubscribeRequest) request,
              (io.grpc.stub.StreamObserver<br.com.poc.grpc.subscription.v1.UnsubscribeResponse>) responseObserver);
          break;
        case METHODID_LIST_SUBSCRIPTIONS:
          serviceImpl.listSubscriptions((br.com.poc.grpc.subscription.v1.ListSubscriptionsRequest) request,
              (io.grpc.stub.StreamObserver<br.com.poc.grpc.subscription.v1.Subscription>) responseObserver);
          break;
        case METHODID_PUBLISH_TO_SUBSCRIBERS:
          serviceImpl.publishToSubscribers((br.com.poc.grpc.subscription.v1.PublishToSubscribersRequest) request,
              (io.grpc.stub.StreamObserver<br.com.poc.grpc.subscription.v1.PublishToSubscribersResponse>) responseObserver);
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
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getSubscribeMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              br.com.poc.grpc.subscription.v1.SubscribeRequest,
              br.com.poc.grpc.subscription.v1.SubscribeResponse>(
                service, METHODID_SUBSCRIBE)))
        .addMethod(
          getUnsubscribeMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              br.com.poc.grpc.subscription.v1.UnsubscribeRequest,
              br.com.poc.grpc.subscription.v1.UnsubscribeResponse>(
                service, METHODID_UNSUBSCRIBE)))
        .addMethod(
          getListSubscriptionsMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              br.com.poc.grpc.subscription.v1.ListSubscriptionsRequest,
              br.com.poc.grpc.subscription.v1.Subscription>(
                service, METHODID_LIST_SUBSCRIPTIONS)))
        .addMethod(
          getPublishToSubscribersMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              br.com.poc.grpc.subscription.v1.PublishToSubscribersRequest,
              br.com.poc.grpc.subscription.v1.PublishToSubscribersResponse>(
                service, METHODID_PUBLISH_TO_SUBSCRIBERS)))
        .build();
  }

  private static abstract class SubscriptionServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    SubscriptionServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return br.com.poc.grpc.subscription.v1.SubscriptionProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("SubscriptionService");
    }
  }

  private static final class SubscriptionServiceFileDescriptorSupplier
      extends SubscriptionServiceBaseDescriptorSupplier {
    SubscriptionServiceFileDescriptorSupplier() {}
  }

  private static final class SubscriptionServiceMethodDescriptorSupplier
      extends SubscriptionServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    SubscriptionServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (SubscriptionServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new SubscriptionServiceFileDescriptorSupplier())
              .addMethod(getSubscribeMethod())
              .addMethod(getUnsubscribeMethod())
              .addMethod(getListSubscriptionsMethod())
              .addMethod(getPublishToSubscribersMethod())
              .build();
        }
      }
    }
    return result;
  }
}
