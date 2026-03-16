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
@Table(name = "refunds")
public class Refund {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long paymentId;
    private String reason;
    private BigDecimal amount;
    private LocalDateTime createdAt;


}

// Refund → { id, paymentId, reason, amount, createdAt }
