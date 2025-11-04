package com.wassimlagnaoui.ecommerce.Payment_Service.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProcessPaymentResponse {
    private Long id;
    private Long orderId;
    private Double amount;
    private String paymentMethod;
    private String status;
    private String createdAt;
}

// { id, orderId, amount, paymentMethod, status, createdAt }
