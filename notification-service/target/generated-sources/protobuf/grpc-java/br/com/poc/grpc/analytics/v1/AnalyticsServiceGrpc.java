package br.com.poc.grpc.analytics.v1;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * AnalyticsService provides aggregated notification metrics.
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.63.0)",
    comments = "Source: analytics/v1/analytics.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class AnalyticsServiceGrpc {

  private AnalyticsServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "analytics.v1.AnalyticsService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<br.com.poc.grpc.analytics.v1.GetStatsRequest,
      br.com.poc.grpc.analytics.v1.GetStatsResponse> getGetStatsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetStats",
      requestType = br.com.poc.grpc.analytics.v1.GetStatsRequest.class,
      responseType = br.com.poc.grpc.analytics.v1.GetStatsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<br.com.poc.grpc.analytics.v1.GetStatsRequest,
      br.com.poc.grpc.analytics.v1.GetStatsResponse> getGetStatsMethod() {
    io.grpc.MethodDescriptor<br.com.poc.grpc.analytics.v1.GetStatsRequest, br.com.poc.grpc.analytics.v1.GetStatsResponse> getGetStatsMethod;
    if ((getGetStatsMethod = AnalyticsServiceGrpc.getGetStatsMethod) == null) {
      synchronized (AnalyticsServiceGrpc.class) {
        if ((getGetStatsMethod = AnalyticsServiceGrpc.getGetStatsMethod) == null) {
          AnalyticsServiceGrpc.getGetStatsMethod = getGetStatsMethod =
              io.grpc.MethodDescriptor.<br.com.poc.grpc.analytics.v1.GetStatsRequest, br.com.poc.grpc.analytics.v1.GetStatsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetStats"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.com.poc.grpc.analytics.v1.GetStatsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.com.poc.grpc.analytics.v1.GetStatsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AnalyticsServiceMethodDescriptorSupplier("GetStats"))
              .build();
        }
      }
    }
    return getGetStatsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<br.com.poc.grpc.analytics.v1.StreamMetricsRequest,
      br.com.poc.grpc.analytics.v1.MetricSnapshot> getStreamMetricsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "StreamMetrics",
      requestType = br.com.poc.grpc.analytics.v1.StreamMetricsRequest.class,
      responseType = br.com.poc.grpc.analytics.v1.MetricSnapshot.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<br.com.poc.grpc.analytics.v1.StreamMetricsRequest,
      br.com.poc.grpc.analytics.v1.MetricSnapshot> getStreamMetricsMethod() {
    io.grpc.MethodDescriptor<br.com.poc.grpc.analytics.v1.StreamMetricsRequest, br.com.poc.grpc.analytics.v1.MetricSnapshot> getStreamMetricsMethod;
    if ((getStreamMetricsMethod = AnalyticsServiceGrpc.getStreamMetricsMethod) == null) {
      synchronized (AnalyticsServiceGrpc.class) {
        if ((getStreamMetricsMethod = AnalyticsServiceGrpc.getStreamMetricsMethod) == null) {
          AnalyticsServiceGrpc.getStreamMetricsMethod = getStreamMetricsMethod =
              io.grpc.MethodDescriptor.<br.com.poc.grpc.analytics.v1.StreamMetricsRequest, br.com.poc.grpc.analytics.v1.MetricSnapshot>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "StreamMetrics"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.com.poc.grpc.analytics.v1.StreamMetricsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.com.poc.grpc.analytics.v1.MetricSnapshot.getDefaultInstance()))
              .setSchemaDescriptor(new AnalyticsServiceMethodDescriptorSupplier("StreamMetrics"))
              .build();
        }
      }
    }
    return getStreamMetricsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<br.com.poc.grpc.analytics.v1.RecordEventRequest,
      br.com.poc.grpc.analytics.v1.RecordEventResponse> getRecordEventMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RecordEvent",
      requestType = br.com.poc.grpc.analytics.v1.RecordEventRequest.class,
      responseType = br.com.poc.grpc.analytics.v1.RecordEventResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<br.com.poc.grpc.analytics.v1.RecordEventRequest,
      br.com.poc.grpc.analytics.v1.RecordEventResponse> getRecordEventMethod() {
    io.grpc.MethodDescriptor<br.com.poc.grpc.analytics.v1.RecordEventRequest, br.com.poc.grpc.analytics.v1.RecordEventResponse> getRecordEventMethod;
    if ((getRecordEventMethod = AnalyticsServiceGrpc.getRecordEventMethod) == null) {
      synchronized (AnalyticsServiceGrpc.class) {
        if ((getRecordEventMethod = AnalyticsServiceGrpc.getRecordEventMethod) == null) {
          AnalyticsServiceGrpc.getRecordEventMethod = getRecordEventMethod =
              io.grpc.MethodDescriptor.<br.com.poc.grpc.analytics.v1.RecordEventRequest, br.com.poc.grpc.analytics.v1.RecordEventResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RecordEvent"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.com.poc.grpc.analytics.v1.RecordEventRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.com.poc.grpc.analytics.v1.RecordEventResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AnalyticsServiceMethodDescriptorSupplier("RecordEvent"))
              .build();
        }
      }
    }
    return getRecordEventMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static AnalyticsServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AnalyticsServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AnalyticsServiceStub>() {
        @java.lang.Override
        public AnalyticsServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AnalyticsServiceStub(channel, callOptions);
        }
      };
    return AnalyticsServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static AnalyticsServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AnalyticsServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AnalyticsServiceBlockingStub>() {
        @java.lang.Override
        public AnalyticsServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AnalyticsServiceBlockingStub(channel, callOptions);
        }
      };
    return AnalyticsServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static AnalyticsServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AnalyticsServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AnalyticsServiceFutureStub>() {
        @java.lang.Override
        public AnalyticsServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AnalyticsServiceFutureStub(channel, callOptions);
        }
      };
    return AnalyticsServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * AnalyticsService provides aggregated notification metrics.
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * GetStats returns aggregated stats for a topic or user (unary).
     * </pre>
     */
    default void getStats(br.com.poc.grpc.analytics.v1.GetStatsRequest request,
        io.grpc.stub.StreamObserver<br.com.poc.grpc.analytics.v1.GetStatsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetStatsMethod(), responseObserver);
    }

    /**
     * <pre>
     * StreamMetrics streams live metric snapshots (server streaming).
     * </pre>
     */
    default void streamMetrics(br.com.poc.grpc.analytics.v1.StreamMetricsRequest request,
        io.grpc.stub.StreamObserver<br.com.poc.grpc.analytics.v1.MetricSnapshot> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getStreamMetricsMethod(), responseObserver);
    }

    /**
     * <pre>
     * RecordEvent ingests a notification lifecycle event (unary).
     * </pre>
     */
    default void recordEvent(br.com.poc.grpc.analytics.v1.RecordEventRequest request,
        io.grpc.stub.StreamObserver<br.com.poc.grpc.analytics.v1.RecordEventResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRecordEventMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service AnalyticsService.
   * <pre>
   * AnalyticsService provides aggregated notification metrics.
   * </pre>
   */
  public static abstract class AnalyticsServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return AnalyticsServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service AnalyticsService.
   * <pre>
   * AnalyticsService provides aggregated notification metrics.
   * </pre>
   */
  public static final class AnalyticsServiceStub
      extends io.grpc.stub.AbstractAsyncStub<AnalyticsServiceStub> {
    private AnalyticsServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AnalyticsServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AnalyticsServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * GetStats returns aggregated stats for a topic or user (unary).
     * </pre>
     */
    public void getStats(br.com.poc.grpc.analytics.v1.GetStatsRequest request,
        io.grpc.stub.StreamObserver<br.com.poc.grpc.analytics.v1.GetStatsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetStatsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * StreamMetrics streams live metric snapshots (server streaming).
     * </pre>
     */
    public void streamMetrics(br.com.poc.grpc.analytics.v1.StreamMetricsRequest request,
        io.grpc.stub.StreamObserver<br.com.poc.grpc.analytics.v1.MetricSnapshot> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getStreamMetricsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * RecordEvent ingests a notification lifecycle event (unary).
     * </pre>
     */
    public void recordEvent(br.com.poc.grpc.analytics.v1.RecordEventRequest request,
        io.grpc.stub.StreamObserver<br.com.poc.grpc.analytics.v1.RecordEventResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRecordEventMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service AnalyticsService.
   * <pre>
   * AnalyticsService provides aggregated notification metrics.
   * </pre>
   */
  public static final class AnalyticsServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<AnalyticsServiceBlockingStub> {
    private AnalyticsServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AnalyticsServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AnalyticsServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * GetStats returns aggregated stats for a topic or user (unary).
     * </pre>
     */
    public br.com.poc.grpc.analytics.v1.GetStatsResponse getStats(br.com.poc.grpc.analytics.v1.GetStatsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetStatsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * StreamMetrics streams live metric snapshots (server streaming).
     * </pre>
     */
    public java.util.Iterator<br.com.poc.grpc.analytics.v1.MetricSnapshot> streamMetrics(
        br.com.poc.grpc.analytics.v1.StreamMetricsRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getStreamMetricsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * RecordEvent ingests a notification lifecycle event (unary).
     * </pre>
     */
    public br.com.poc.grpc.analytics.v1.RecordEventResponse recordEvent(br.com.poc.grpc.analytics.v1.RecordEventRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRecordEventMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service AnalyticsService.
   * <pre>
   * AnalyticsService provides aggregated notification metrics.
   * </pre>
   */
  public static final class AnalyticsServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<AnalyticsServiceFutureStub> {
    private AnalyticsServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AnalyticsServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AnalyticsServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * GetStats returns aggregated stats for a topic or user (unary).
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<br.com.poc.grpc.analytics.v1.GetStatsResponse> getStats(
        br.com.poc.grpc.analytics.v1.GetStatsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetStatsMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * RecordEvent ingests a notification lifecycle event (unary).
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<br.com.poc.grpc.analytics.v1.RecordEventResponse> recordEvent(
        br.com.poc.grpc.analytics.v1.RecordEventRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRecordEventMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_STATS = 0;
  private static final int METHODID_STREAM_METRICS = 1;
  private static final int METHODID_RECORD_EVENT = 2;

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
        case METHODID_GET_STATS:
          serviceImpl.getStats((br.com.poc.grpc.analytics.v1.GetStatsRequest) request,
              (io.grpc.stub.StreamObserver<br.com.poc.grpc.analytics.v1.GetStatsResponse>) responseObserver);
          break;
        case METHODID_STREAM_METRICS:
          serviceImpl.streamMetrics((br.com.poc.grpc.analytics.v1.StreamMetricsRequest) request,
              (io.grpc.stub.StreamObserver<br.com.poc.grpc.analytics.v1.MetricSnapshot>) responseObserver);
          break;
        case METHODID_RECORD_EVENT:
          serviceImpl.recordEvent((br.com.poc.grpc.analytics.v1.RecordEventRequest) request,
              (io.grpc.stub.StreamObserver<br.com.poc.grpc.analytics.v1.RecordEventResponse>) responseObserver);
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
          getGetStatsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              br.com.poc.grpc.analytics.v1.GetStatsRequest,
              br.com.poc.grpc.analytics.v1.GetStatsResponse>(
                service, METHODID_GET_STATS)))
        .addMethod(
          getStreamMetricsMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              br.com.poc.grpc.analytics.v1.StreamMetricsRequest,
              br.com.poc.grpc.analytics.v1.MetricSnapshot>(
                service, METHODID_STREAM_METRICS)))
        .addMethod(
          getRecordEventMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              br.com.poc.grpc.analytics.v1.RecordEventRequest,
              br.com.poc.grpc.analytics.v1.RecordEventResponse>(
                service, METHODID_RECORD_EVENT)))
        .build();
  }

  private static abstract class AnalyticsServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    AnalyticsServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return br.com.poc.grpc.analytics.v1.AnalyticsProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("AnalyticsService");
    }
  }

  private static final class AnalyticsServiceFileDescriptorSupplier
      extends AnalyticsServiceBaseDescriptorSupplier {
    AnalyticsServiceFileDescriptorSupplier() {}
  }

  private static final class AnalyticsServiceMethodDescriptorSupplier
      extends AnalyticsServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    AnalyticsServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (AnalyticsServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new AnalyticsServiceFileDescriptorSupplier())
              .addMethod(getGetStatsMethod())
              .addMethod(getStreamMetricsMethod())
              .addMethod(getRecordEventMethod())
              .build();
        }
      }
    }
    return result;
  }
}
