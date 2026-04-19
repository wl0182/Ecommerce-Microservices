package com.wassimlagnaoui.ecommerce.Shipping_Service.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ShipRequest {
    @NotNull(message = "Order ID cannot be null")
    private Long orderId;
    @NotNull(message = "User ID cannot be null")
    private Long userId;
    @NotNull(message = "Address ID cannot be null")
    private Long addressId;

}// { orderId, userId, addressId }
