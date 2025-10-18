package com.wassimlagnaoui.ecommerce.order_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrdersForUserResponse {
    private Long id;
    private double totalAmount;
    private String status;
    private String createdAt;
} // [{ id, totalAmount, status, createdAt }]
