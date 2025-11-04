package com.wassimlagnaoui.ecommerce.Payment_Service.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IssueRefundResponse {
    private Long id;
    private Long orderId;
    private Double amount;
    private String status;
    private LocalDateTime refundedAt;
}

// { id, orderId, amount, status:"REFUNDED", refundedAt }