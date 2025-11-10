package com.wassimlagnaoui.ecommerce.Shipping_Service.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentDTO {
    private Long orderId;
    private String carrier;
    private String trackingNumber;
    private String status;
    private String estimatedDelivery;
    private String actualDelivery;
    private String createdAt;

} // { orderId, carrier, trackingNumber, status, estimatedDelivery, actualDelivery, createdAt }
