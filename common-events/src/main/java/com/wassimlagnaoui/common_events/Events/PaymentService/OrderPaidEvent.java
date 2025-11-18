package com.wassimlagnaoui.common_events.Events.PaymentService;


import com.wassimlagnaoui.common_events.Events.BaseEvent;
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
public class OrderPaidEvent extends BaseEvent {
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentStatus;
    private Instant paidAt;
}// { orderId, userId, amount, paymentMethod, paymentStatus, paidAt }

