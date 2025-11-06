package com.wassimlagnaoui.ecommerce.order_service.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {
    private Long productId;
    private String productName;
    private String productDescription;
    private String productCategory;
    private int quantity;
    private BigDecimal price;
}
