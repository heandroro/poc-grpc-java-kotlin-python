import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

// ============================================================
// 1. Read-Modify-Write — AtomicInteger vs int
// ============================================================

// ❌ Race condition: count++ não é atômico
class UnsafeCounter {
    private int count = 0;
    public void increment() { count++; }  // read → modify → write
    public int get()        { return count; }
}

// ✅ Atômico com AtomicInteger
class SafeCounter {
    private final AtomicInteger count = new AtomicInteger(0);
    public void increment() { count.incrementAndGet(); }
    public int get()        { return count.get(); }
}

// ✅ LongAdder — menor contenção com muitas threads
class HighThroughputCounter {
    private final LongAdder count = new LongAdder();
    public void increment() { count.increment(); }
    public long sum()       { return count.sum(); }
}

// ============================================================
// 2. Check-Then-Act — computeIfAbsent
// ============================================================

class OrderCache {
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    // ❌ Race condition: check e put separados
    public String getUnsafe(String key) {
        if (!cache.containsKey(key)) {       // Thread A passa aqui
            cache.put(key, loadFromDb(key)); // Thread B já inseriu
        }
        return cache.get(key);
    }

    // ✅ computeIfAbsent é atômico
    public String getSafe(String key) {
        return cache.computeIfAbsent(key, this::loadFromDb);
    }

    private String loadFromDb(String key) { return "value-" + key; }
}

// ============================================================
// 3. Seção crítica com ReentrantLock
// ============================================================

class TransferService {
    private final Map<String, Double> balances = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    // ❌ synchronized — causa pinning com Virtual Threads
    public synchronized void transferUnsafe(String from, String to, double amount) {
        double fromBalance = balances.getOrDefault(from, 0.0);
        if (fromBalance < amount) throw new IllegalStateException("Insufficient funds");
        balances.put(from, fromBalance - amount);
        balances.put(to, balances.getOrDefault(to, 0.0) + amount);
    }

    // ✅ ReentrantLock — compatível com Virtual Threads
    public void transfer(String from, String to, double amount) {
        lock.lock();
        try {
            double fromBalance = balances.getOrDefault(from, 0.0);
            if (fromBalance < amount) throw new IllegalStateException("Insufficient funds");
            balances.put(from, fromBalance - amount);
            balances.put(to, balances.getOrDefault(to, 0.0) + amount);
        } finally {
            lock.unlock();
        }
    }
}

// ============================================================
// 4. Read/Write com StampedLock (otimista)
// ============================================================

class PricingService {
    private double price = 100.0;
    private final StampedLock lock = new StampedLock();

    // Leitura otimista — sem bloquear escritores
    public double getPrice() {
        long stamp = lock.tryOptimisticRead();
        double value = price;
        if (!lock.validate(stamp)) {         // alguém escreveu entre tryOptimistic e validate
            stamp = lock.readLock();
            try { value = price; } finally { lock.unlockRead(stamp); }
        }
        return value;
    }

    // Escrita exclusiva
    public void updatePrice(double newPrice) {
        long stamp = lock.writeLock();
        try { price = newPrice; } finally { lock.unlockWrite(stamp); }
    }
}

// ============================================================
// 5. Estado imutável elimina race conditions
// ============================================================

// ✅ Record imutável — sem necessidade de sincronização
record OrderSnapshot(String id, List<String> items, double total) {
    OrderSnapshot {
        items = List.copyOf(items); // cópia defensiva imutável
    }
}

// ============================================================
// 6. Coleções thread-safe
// ============================================================

class SessionRegistry {
    // ✅ ConcurrentHashMap para operações concorrentes
    private final Map<String, String> sessions = new ConcurrentHashMap<>();

    // ✅ CopyOnWriteArrayList para listas com mais leituras que escritas
    private final List<String> activeIds = new CopyOnWriteArrayList<>();

    public void register(String sessionId, String userId) {
        sessions.put(sessionId, userId);
        activeIds.add(sessionId);
    }

    public void remove(String sessionId) {
        sessions.remove(sessionId);
        activeIds.remove(sessionId);
    }
}
