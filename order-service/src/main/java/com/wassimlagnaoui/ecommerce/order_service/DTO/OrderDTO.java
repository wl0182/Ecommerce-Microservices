package com.wassimlagnaoui.ecommerce.order_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {
    private Long id;
    private Long userId;
    private List<OrderItemResponse> items;
    private Double totalAmount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

} // { id, userId, items:[{ productId, quantity, price }], totalAmount, status, createdAt, updatedAt }
