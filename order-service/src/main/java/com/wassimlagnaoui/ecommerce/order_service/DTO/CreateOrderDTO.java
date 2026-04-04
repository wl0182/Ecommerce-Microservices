package com.wassimlagnaoui.ecommerce.order_service.DTO;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderDTO {

    @NotNull(message = "User ID cannot be null")
    private Long userId;
    private List<OrderItemRequest> items;
    @NotBlank(message = "Payment method cannot be blank") 
    private String paymentMethod;
    @NotNull(message = "Address ID cannot be null")
    private Long addressId;



} // { userId, items:[{ productId, quantity }], paymentMethod, addressId }
