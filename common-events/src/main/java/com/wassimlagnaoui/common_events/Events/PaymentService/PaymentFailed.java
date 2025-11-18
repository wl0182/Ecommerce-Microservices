package com.wassimlagnaoui.common_events.Events.PaymentService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentFailed {
    private String paymentId;
    private Long orderId;
    private String userId;
    private BigDecimal amount;
    private Instant failedAt;
} // { paymentId, orderId, userId, amount, failedAt }
