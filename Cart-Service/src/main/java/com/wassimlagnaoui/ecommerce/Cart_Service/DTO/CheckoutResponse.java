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
public class CheckoutResponse {
    private Long userId;
    private Long orderId;
    private String status;
    private Double totalAmount;
    private String createdAt;

} // { userId ,orderId, status, totalAmount, createdAt }
