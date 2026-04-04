package com.wassimlagnaoui.ecommerce.Cart_Service.DTO.RestDTOs;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {
    @NotNull(message = "Product ID cannot be null")
    private Long productId;
    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}
