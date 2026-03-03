package com.wassimlagnaoui.ecommerce.Cart_Service.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartDTO {

    private long userId;
    private List<CartItemDTO> items;
    private BigDecimal totalAmount;

}

// { userId, items:[{ productId, name, quantity, price }], totalAmount }