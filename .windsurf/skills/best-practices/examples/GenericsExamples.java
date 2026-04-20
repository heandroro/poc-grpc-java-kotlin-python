import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

// ============================================================
// 1. Bounded Types e Wildcards (PECS)
// ============================================================

// Upper bound: aceita Number e subtypes
class Statistics {
    public <T extends Number> double average(List<T> values) {
        return values.stream().mapToDouble(Number::doubleValue).average().orElse(0);
    }

    // Producer Extends — leitura
    public double sumAmounts(List<? extends Money> amounts) {
        return amounts.stream().mapToDouble(m -> m.amount().doubleValue()).sum();
    }

    // Consumer Super — escrita
    public void fillDefaults(List<? super String> list, int count) {
        for (int i = 0; i < count; i++) list.add("default");
    }
}

// ============================================================
// 2. Result<T> — operações que podem falhar
// ============================================================

sealed interface Result<T> permits Result.Success, Result.Failure {

    record Success<T>(T value) implements Result<T> {}
    record Failure<T>(String errorCode, String message) implements Result<T> {}

    static <T> Result<T> success(T value)                       { return new Success<>(value); }
    static <T> Result<T> failure(String code, String message)   { return new Failure<>(code, message); }

    default boolean isSuccess() { return this instanceof Success; }

    default T getOrThrow() {
        return switch (this) {
            case Success<T> s -> s.value();
            case Failure<T> f -> throw new IllegalStateException(f.errorCode() + ": " + f.message());
        };
    }

    default <U> Result<U> map(Function<T, U> mapper) {
        return switch (this) {
            case Success<T> s -> Result.success(mapper.apply(s.value()));
            case Failure<T> f -> Result.failure(f.errorCode(), f.message());
        };
    }
}

// ============================================================
// 3. Page<T> genérico para paginação
// ============================================================

record Page<T>(List<T> content, int pageNumber, int pageSize, long totalElements) {
    public int totalPages()  { return (int) Math.ceil((double) totalElements / pageSize); }
    public boolean hasNext() { return pageNumber < totalPages() - 1; }

    public <U> Page<U> map(Function<T, U> mapper) {
        return new Page<>(content.stream().map(mapper).toList(), pageNumber, pageSize, totalElements);
    }
}

// ============================================================
// 4. Cache<K, V> genérico com TTL
// ============================================================

class Cache<K, V> {
    private final Map<K, CacheEntry<V>> store = new ConcurrentHashMap<>();
    private final Duration ttl;

    Cache(Duration ttl) { this.ttl = ttl; }

    public void put(K key, V value) {
        store.put(key, new CacheEntry<>(value, Instant.now().plus(ttl)));
    }

    public Optional<V> get(K key) {
        return Optional.ofNullable(store.get(key))
            .filter(e -> e.expiresAt().isAfter(Instant.now()))
            .map(CacheEntry::value);
    }

    private record CacheEntry<V>(V value, Instant expiresAt) {}
}

// ============================================================
// 5. Repository<T, ID> genérico
// ============================================================

interface Repository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void delete(T entity);
}

// ============================================================
// 6. Records e Sealed Interfaces genéricos
// ============================================================

record Pair<A, B>(A first, B second) {
    static <A, B> Pair<A, B> of(A a, B b) { return new Pair<>(a, b); }

    public <C> Pair<C, B> mapFirst(Function<A, C> mapper) {
        return new Pair<>(mapper.apply(first), second);
    }
}

record ApiResponse<T>(T data, String requestId, Instant timestamp) {
    static <T> ApiResponse<T> of(T data, String requestId) {
        return new ApiResponse<>(data, requestId, Instant.now());
    }
}

// ============================================================
// 7. CQRS com Command<R> genérico
// ============================================================

sealed interface Command<R> permits CreateOrderCommand, CancelOrderCommand {}

record CreateOrderCommand(String customerId, List<String> items) implements Command<String> {}
record CancelOrderCommand(String orderId, String reason)           implements Command<Void> {}

interface CommandHandler<C extends Command<R>, R> {
    R handle(C command);
}
