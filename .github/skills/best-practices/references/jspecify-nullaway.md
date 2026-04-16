# JSpecify e NullAway - Guia Rápido

## Visão Geral

JSpecify e NullAway permitem null safety em tempo de compilação para Java 25, transformando NullPointerException de erro de runtime em erro de compilação.

## Conceitos Fundamentais

### @NullMarked

Define que todos os tipos em um pacote ou classe são não-nulos por padrão:

```java
@NullMarked
package com.example.app;

@NullMarked
public class OrderService {
    // Todos os parâmetros e retornos são @NonNull por padrão
    public Order create(String id) { ... }  // id e retorno são não-nulos
}
```

### @Nullable

Marca explicitamente onde null é permitido:

```java
@NullMarked
public class CustomerService {
    // Retorno pode ser null — explicitamente marcado
    public @Nullable Customer findById(String id) {
        return repository.findById(id).orElse(null);
    }
    
    // Parâmetro pode receber null
    public void processEmail(@Nullable String email) {
        if (email != null) { ... }
    }
}
```

## Configuração Maven

```xml
<dependency>
    <groupId>org.jspecify</groupId>
    <artifactId>jspecify</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Configuração NullAway (Error Prone)

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <compilerArgs>
            <arg>-XDcompilePolicy=simple</arg>
            <arg>-Xplugin:ErrorProne -Xep:NullAway:ERROR</arg>
            <arg>-XepOpt:NullAway:AnnotatedPackages=com.example.app</arg>
            <arg>-XepOpt:NullAway:AcknowledgeRestrictiveAnnotations=true</arg>
        </compilerArgs>
        <annotationProcessorPaths>
            <path>
                <groupId>com.uber.nullaway</groupId>
                <artifactId>nullaway</artifactId>
                <version>0.11.0</version>
            </path>
            <path>
                <groupId>com.google.errorprone</groupId>
                <artifactId>error_prone_core</artifactId>
                <version>2.24.0</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

## Boas Práticas

1. **Anotar pacotes raiz com @NullMarked** — define padrão não-nulo para todo o código
2. **Usar @Nullable explicitamente** — documenta onde null é esperado
3. **Nunca retornar null em vez de Optional** — Optional é naturalmente suportado
4. **Configurar NullAway como ERROR** — falha de build em violações

## Spring Boot 4 + JSpecify

Spring Framework 7 e Spring Boot 4 já são @NullMarked. Ao usar APIs do Spring com JSpecify:

- Constructor injection garante dependências não-nulas
- Métodos que retornam Optional são respeitados
- @Nullable em parâmetros opcionais deve ser marcado explicitamente
