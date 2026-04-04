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
    @NotBlank(message = "User ID cannot be blank")
    private Long userId;
    @NotBlank(message = "Address ID cannot be blank")
    private Long addressId;

}// { orderId, userId, addressId }
