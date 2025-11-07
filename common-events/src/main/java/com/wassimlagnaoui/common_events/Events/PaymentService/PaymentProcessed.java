package com.wassimlagnaoui.common_events.Events.PaymentService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentProcessed {
    private String paymentId;
    private Long orderId;
    private Long userId;
    private double amount;
    private String status;
    private String paymentMethod;
    private Instant createdAt;
}// { paymentId, orderId, userId, amount, status, paymentMethod, createdAt }
