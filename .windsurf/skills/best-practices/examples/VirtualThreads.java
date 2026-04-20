// Examples: Virtual Threads, Structured Concurrency, Scoped Values
// Source: best-practices/SKILL.md

package com.example.app;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import jdk.incubator.concurrent.ScopedValue;  // Preview API

class VirtualThreadsExamples {

    // ✅ Virtual Threads Executor
    void virtualThreadsExample() {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var future1 = executor.submit(() -> fetchFromDatabase());
            var future2 = executor.submit(() -> callExternalApi());
            
            var result1 = future1.get();
            var result2 = future2.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ✅ Structured Concurrency
    record OrderDetails(Order order, Customer customer) {}
    
    OrderDetails fetchDetails(java.util.UUID orderId, java.util.UUID customerId) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var orderTask = scope.fork(() -> fetchOrder(orderId));
            var customerTask = scope.fork(() -> fetchCustomer(customerId));

            scope.join().throwIfFailed();

            return new OrderDetails(orderTask.get(), customerTask.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ✅ ReentrantLock (ao invés de synchronized)
    private final ReentrantLock lock = new ReentrantLock();

    void safeConcurrentOperation() {
        lock.lock();
        try {
            // código longo...
        } finally {
            lock.unlock();
        }
    }

    // ❌ Synchronized - EVITAR com Virtual Threads (pode causar pinning)
    // synchronized void badExample() { ... }

    // Placeholder methods
    String fetchFromDatabase() { return "db"; }
    String callExternalApi() { return "api"; }
    Order fetchOrder(java.util.UUID id) { return new Order(); }
    Customer fetchCustomer(java.util.UUID id) { return new Customer(); }
    
    class Order {}
    class Customer {}
}
