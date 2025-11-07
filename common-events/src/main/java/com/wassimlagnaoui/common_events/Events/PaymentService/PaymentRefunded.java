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
public class PaymentRefunded {
    private Long paymentId;
    private Long orderId;
    private BigDecimal amount;
    private Instant refundedAt;
    private String reason;

} // { paymentId, orderId, amount, refundedAt, reason }
