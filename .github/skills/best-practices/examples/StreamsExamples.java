// Examples: Streams - Best and Worst Practices
// Source: best-practices/SKILL.md

package com.example.app;

import java.util.*;
import java.util.stream.Collectors;

class StreamsExamples {

    record User(String name, boolean active, Plan plan, List<String> permissions) {}
    record Order(double total) {}
    enum Plan { FREE, PREMIUM }

    // ========== ✅ BOM: Streams apropriados ==========

    void goodStreamExamples(List<User> users, List<Order> orders, List<String> list) {
        // Use streams para transformações de coleções
        var activeUsers = users.stream()
            .filter(User::active)
            .map(User::name)
            .toList();  // Java 16+ — preferir sobre collect(Collectors.toList())

        // Use Optional com streams para evitar nulls
        var firstPremium = users.stream()
            .filter(u -> u.plan() == Plan.PREMIUM)
            .findFirst();  // retorna Optional<User>

        // Use flatMap para aninhar coleções
        var allPermissions = users.stream()
            .flatMap(u -> u.permissions().stream())
            .distinct()
            .toList();

        // Use reduce para agregações simples
        var totalValue = orders.stream()
            .mapToDouble(Order::total)
            .sum();

        // Use collect com downstream collectors
        var usersByPlan = users.stream()
            .collect(Collectors.groupingBy(User::plan, Collectors.counting()));
    }

    // ========== ❌ RUIM: Anti-patterns ==========

    void badStreamExamples(List<String> list, String target, List<User> users) {
        // ❌ Não usar stream para operações simples (overhead)
        // if (list.stream().anyMatch(x -> x.equals(target))) { ... }  // ruim
        if (list.contains(target)) { }  // bom

        // ❌ Não usar streams quando precisa de índice ou controle de fluxo
        // IntStream.range(0, list.size()).forEach(i -> { ... });  // ruim
        for (int i = 0; i < list.size(); i++) { }  // bom

        // ❌ Não modificar estado externo em forEach (side effects)
        // AtomicInteger count = new AtomicInteger(0);
        // list.stream().forEach(x -> count.incrementAndGet());  // ruim
        long count = list.stream().count();  // bom

        // ❌ Não criar streams desnecessários
        // list.stream().map(x -> x.toString()).collect(Collectors.joining());  // ruim
        String joined = String.join(",", list);  // bom

        // ❌ Não usar findFirst().get() sem verificação
        // var first = users.stream().findFirst().get();  // ruim
        var first = users.stream().findFirst().orElseThrow();  // bom — explícito
    }
}
