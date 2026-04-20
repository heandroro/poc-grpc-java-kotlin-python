# MapStruct - Guia de Uso

## Visão Geral

MapStruct é um gerador de código que automatiza mapeamento entre objetos (DTOs ↔ Entities), eliminando código boilerplate e garantindo type safety em tempo de compilação.

## Anotações Principais

### @Mapper

Marca a interface como mapper e configura geração de código pelo annotation processor.
- `componentModel = "spring"` — gera `@Component`, permite injeção via construtor.
- Métodos sem anotação: MapStruct faz matching por nome de campo automaticamente.
- Métodos de lista não precisam de anotação: `List<OrderResponse> toDtoList(List<Order>)` é gerado automaticamente.

### @Mapping

| Atributo | Uso |
|----------|-----|
| `ignore = true` | Não mapear o campo (ex: `id`, `createdAt`) |
| `constant = "PENDING"` | Preencher com valor fixo |
| `source = "x", target = "y"` | Campos com nomes diferentes |
| `dateFormat = "yyyy-MM-dd"` | Converter `Instant`/`LocalDate` para `String` |

### @Named e qualifiedByName

Usado para conversões customizadas entre tipos não mapeáveis automaticamente (ex: `String` → Value Object).
- `@Named("toEmail")` — nomeia o método conversor.
- `qualifiedByName = "toEmail"` no `@Mapping` — indica qual conversor usar.
- Implementado como `default` na própria interface.

## Regras de Uso

1. **Proibir mapeamento manual** — Sempre usar MapStruct para mapeamento entre camadas
2. **Métodos default para Value Objects** — Usar @Named para conversões customizadas
3. **Testar mappers gerados** — Verificar comportamento com testes de integração
4. **Não usar em loops** — Mapear coleções via método de lista, não um por um

## Configuração Maven

```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.6.0</version>
</dependency>

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.13.0</version>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct-processor</artifactId>
                <version>1.6.0</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

## Casos Especiais

### Mapeamento de Enums

- `@ValueMapping(source = "DRAFT", target = "PENDING")` — renomear valor de enum.
- `@ValueMapping(source = MappingConstants.NULL, target = "UNKNOWN")` — tratar `null` como valor default.
- Sem anotação: MapStruct mapeia por nome exato; erro de compilação se houver valor sem correspondência.

### Atualização Completa — PUT (`@MappingTarget`)

`@MappingTarget` faz o mapper sobrescrever **todos os campos** da entity existente com os valores do DTO, incluindo `null`. Usar para `PUT`.
- Retorno `void` — mapper muta a entity em vez de criar nova instância.
- Sempre `ignore = true` em `id` e campos de auditoria (`createdAt`, `updatedBy`).
- No service: `mapper.updateFromRequest(dto, entity)` → `repository.save(entity)`.

### Atualização Parcial — PATCH (`NullValuePropertyMappingStrategy.IGNORE`)

`NullValuePropertyMappingStrategy.IGNORE` faz o mapper **ignorar campos nulos** do DTO — o campo da entity mantém seu valor atual. Usar para `PATCH`.
- Configurado no `@Mapper`, não no `@Mapping` individual.
- No service: mesmo fluxo do PUT — `mapper.patchFromRequest(dto, entity)` → `repository.save(entity)`.

**Comparativo:**

| | PUT | PATCH |
|--|-----|-------|
| DTO envia `name: null` | Entity fica com `name = null` | Entity mantém o `name` atual |
| Estratégia | padrão (`SET`) | `NullValuePropertyMappingStrategy.IGNORE` |
| `@MappingTarget` | ✅ | ✅ |
| Campos ignorados | Apenas os anotados com `ignore = true` | `ignore = true` + campos nulos do DTO |

> **Atenção**: com `IGNORE`, não é possível zerar intencionalmente um campo via PATCH enviando `null`. Se esse caso existir, use `Optional<T>` no DTO ou implemente o merge manualmente.

## Exemplos

Ver `examples/OrderMapper.java` — `OrderMapper` básico, `@Named`, `@ValueMapping`, PUT com `@MappingTarget`, PATCH com `NullValuePropertyMappingStrategy.IGNORE`.
