package com.wassimlagnaoui.ecommerce.Cart_Service.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemDTO {
    private Long cartId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
} // Json : { id, cartId, productId, productName, quantity, price }
