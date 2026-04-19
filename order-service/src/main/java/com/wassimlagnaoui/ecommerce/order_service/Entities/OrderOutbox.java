package com.wassimlagnaoui.ecommerce.order_service.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "order_outbox", indexes = {
        @Index(name = "idx_order_outbox_status", columnList = "status"),
        @Index(name = "idx_order_outbox_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderOutbox {

    @Id
    private UUID id;
    @Column(name = "aggregate_id", nullable = false)
    private Long aggregateId; // ID of the Order aggregate

    @Column(columnDefinition = "TEXT", nullable = false)
    private String eventType; // Type of the event (e.g., "OrderCreated", "OrderUpdated")

    @Column(name = "payload",nullable = false)
    private String payload; // JSON representation of the event data

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EventStatus status; // Status of the event (e.g., PENDING, PROCESSED, FAILED)

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt; // Timestamp when the event was created

    @Column(name = "processed_at")
    private LocalDateTime processedAt; // Timestamp when the event was processed

    @Column(name = "retry_count")
    private Integer retryCount=0; // Number of times the event has been retried

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage; // Error message in case of processing failure


}
