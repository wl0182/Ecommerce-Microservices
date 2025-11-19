package com.wassimlagnaoui.ecommerce.Shipping_Service.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatusResponse {
    private Long id;
    private Long orderId;
    private String trackingNumber;
    private String status;
    private String updatedAt;
}// { id, orderId, trackingNumber, status, updatedAt }
