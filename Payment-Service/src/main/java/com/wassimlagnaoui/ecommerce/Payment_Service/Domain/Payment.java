package com.wassimlagnaoui.ecommerce.Payment_Service.Domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long orderId;
    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method; // CARD or WALLET

    private String transactionId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // INITIATED, SUCCESS, FAILED, REFUNDED

    private String createdAt;
    private String updatedAt;

}

/*
 * Payment → { id, orderId, amount, method[CARD/WALLET], transactionId, status[INITIATED/SUCCESS/FAILED/REFUNDED], createdAt, updatedAt }
 * Refund → { id, paymentId, reason, amount, createdAt }
 */