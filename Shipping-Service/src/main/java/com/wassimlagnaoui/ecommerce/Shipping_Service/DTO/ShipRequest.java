package com.wassimlagnaoui.ecommerce.Shipping_Service.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ShipRequest {
    private Long orderId;
    private Long userId;
    private Long addressId;

}// { orderId, userId, addressId }
