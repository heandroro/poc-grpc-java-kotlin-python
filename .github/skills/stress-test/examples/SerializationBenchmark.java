import com.fasterxml.jackson.databind.ObjectMapper;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;
import java.util.UUID;

// ============================================================
// JMH — Microbenchmark de serialização JSON
// ============================================================

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class SerializationBenchmark {

    private ObjectMapper objectMapper;
    private OrderResponse orderResponse;
    private String orderJson;

    @Setup
    public void setup() throws Exception {
        objectMapper = new ObjectMapper();
        orderResponse = new OrderResponse(
            UUID.randomUUID().toString(),
            "cust-1",
            "PENDING",
            100.0
        );
        orderJson = objectMapper.writeValueAsString(orderResponse);
    }

    // Serialização: objeto → JSON
    @Benchmark
    public void serialize(Blackhole bh) throws Exception {
        bh.consume(objectMapper.writeValueAsString(orderResponse));
    }

    // Deserialização: JSON → objeto
    @Benchmark
    public void deserialize(Blackhole bh) throws Exception {
        bh.consume(objectMapper.readValue(orderJson, OrderResponse.class));
    }
}

// ============================================================
// JMH — Microbenchmark de processamento de coleção
// ============================================================

@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
@Fork(1)
public class OrderCalculationBenchmark {

    @Param({"10", "100", "1000"})
    private int itemCount;

    private java.util.List<OrderItem> items;

    @Setup
    public void setup() {
        items = java.util.stream.IntStream.range(0, itemCount)
            .mapToObj(i -> new OrderItem("prod-" + i, i + 1, 10.0 * (i + 1)))
            .toList();
    }

    @Benchmark
    public double calculateTotal(Blackhole bh) {
        var total = items.stream()
            .mapToDouble(item -> item.quantity() * item.price())
            .sum();
        bh.consume(total);
        return total;
    }
}

record OrderResponse(String id, String customerId, String status, double total) {}
record OrderItem(String productId, int quantity, double price) {}
