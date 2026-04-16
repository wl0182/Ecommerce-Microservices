package com.wassimlagnaoui.ecommerce.order_service.DTO;


import com.wassimlagnaoui.ecommerce.order_service.Entities.PaymentMethod;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderDTO {


    private List<OrderItemRequest> items;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    @NotNull(message = "Address ID cannot be null")
    private Long addressId;



} // { userId, items:[{ productId, quantity }], paymentMethod, addressId }
