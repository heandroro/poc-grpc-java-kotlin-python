// Examples: MapStruct Mappers
// Source: best-practices/SKILL.md

package com.example.app;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ValueMapping;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

// DTO
record CreateOrderRequest(
    String customerEmail,
    List<OrderItemRequest> items
) {}

record OrderItemRequest(String productId, int quantity) {}
record OrderResponse(UUID id, String customerEmail, String status) {}

// Domain Entity
class Order {
    private UUID id;
    private Email customerEmail;
    private List<OrderItem> items;
    private OrderStatus status;
    
    // getters/setters omitted
}

class OrderItem {
    private String productId;
    private int quantity;
}

class Email {
    private final String value;
    public Email(String value) { this.value = value; }
    public String value() { return value; }
}

// Mapper
@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "customerEmail", source = "customerEmail", qualifiedByName = "toEmail")
    Order toEntity(CreateOrderRequest request);

    @Mapping(target = "customerEmail", source = "customerEmail", qualifiedByName = "fromEmail")
    OrderResponse toResponse(Order order);

    @Named("toEmail")
    default Email toEmail(String email) {
        return new Email(email);
    }

    @Named("fromEmail")
    default String fromEmail(Email email) {
        return email.value();
    }
}

// ============================================================
// @ValueMapping — mapeamento de Enums
// ============================================================

enum OrderStatusDto    { DRAFT, CONFIRMED, SHIPPED }
enum OrderStatusDomain { PENDING, CONFIRMED, SHIPPED, UNKNOWN }

@Mapper
interface StatusMapper {

    @ValueMapping(source = "DRAFT",               target = "PENDING")  // renomear valor
    @ValueMapping(source = MappingConstants.NULL, target = "UNKNOWN")  // null → valor default
    OrderStatusDomain map(OrderStatusDto dto);
}

// ============================================================
// PUT — @MappingTarget sobrescreve todos os campos (inclusive null)
// ============================================================

record UpdateOrderRequest(String customerEmail, String notes, Instant updatedAt) {}

@Mapper(componentModel = "spring")
interface OrderUpdateMapper {

    @Mapping(target = "id",     ignore = true)   // nunca sobrescrever id
    @Mapping(target = "status", ignore = true)   // status muda só por comportamento de domínio
    void updateFromRequest(UpdateOrderRequest request, @MappingTarget Order order);
    //   ^ void: muta a entity existente em vez de criar nova instância
}

// ============================================================
// PATCH — NullValuePropertyMappingStrategy.IGNORE
//         campos null no DTO são ignorados; entity mantém valor atual
// ============================================================

record PatchOrderRequest(String customerEmail, String notes) {}

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
interface OrderPatchMapper {

    @Mapping(target = "id",     ignore = true)
    @Mapping(target = "status", ignore = true)
    void patchFromRequest(PatchOrderRequest request, @MappingTarget Order order);
    //   se PatchOrderRequest.customerEmail == null, order.customerEmail não é alterado
}
