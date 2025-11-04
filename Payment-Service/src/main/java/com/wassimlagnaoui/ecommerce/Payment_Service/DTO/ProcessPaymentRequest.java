package com.wassimlagnaoui.ecommerce.Payment_Service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessPaymentRequest {
    private Long orderId;
    private Long userId;
    private Double amount;
    private String paymentMethod;

}

// { orderId, userId, amount, paymentMethod }