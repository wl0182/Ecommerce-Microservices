package com.wassimlagnaoui.ecommerce.order_service.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCreatedResponse {

    private Long id;
    private Long userId;
    private List<OrderItemResponse> items;
    private double totalAmount;
    private String status;
    private String createdAt;

} // { id, userId, items:[{ productId, quantity, price }], totalAmount, status, createdAt }
