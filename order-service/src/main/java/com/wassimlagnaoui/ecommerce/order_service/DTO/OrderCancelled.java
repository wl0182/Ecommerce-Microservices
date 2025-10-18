package com.wassimlagnaoui.ecommerce.order_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCancelled {
    private Long id;
    private String status;
    private String updatedAt;
}// { id, status:"CANCELLED", updatedAt }
