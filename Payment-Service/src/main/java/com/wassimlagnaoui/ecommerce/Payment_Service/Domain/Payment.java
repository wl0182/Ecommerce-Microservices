package com.wassimlagnaoui.ecommerce.Payment_Service.Domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments", uniqueConstraints = {@UniqueConstraint(columnNames = {"orderId"})})
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long orderId;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method; // CARD or WALLET

    private String transactionId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // INITIATED, SUCCESS, FAILED, REFUNDED

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

/*
 * Payment → { id, orderId, amount, method[CARD/WALLET], transactionId, status[INITIATED/SUCCESS/FAILED/REFUNDED], createdAt, updatedAt }
 * Refund → { id, paymentId, reason, amount, createdAt }
 *
 * to add uniques contstraint 1 payment par orderId @UniqueConstraint(columnNames = {"orderId"})
 */