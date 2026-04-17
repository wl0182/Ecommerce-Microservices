package com.wassimlagnaoui.ecommerce.product_service.Domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Table(name = "product_outbox")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ProductOutbox {
    @Id
    private UUID id;
    @Column(name = "aggregate_id")
    private Long aggregateId;
    @Column(name = "event_type",nullable = false)
    private String eventType;
    @Column(name = "payload", nullable = false)
    private String payload;
    @Enumerated(EnumType.STRING)
    private EventStatus status;// PENDING, PROCESSED, FAILED
    @Column(name = "created_at")
    private LocalDateTime createdAt;



    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "retry_count")
    private Integer retryCount=0;
    @Column(name = "error_message")
    private String errorMessage;

}

