package com.wassimlagnaoui.ecommerce.Payment_Service.Domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment_outbox")
public class PaymentOutbox {
    @Id
    private UUID id;
    @Column(nullable = false)
    private Long aggregateId;
    @Column(nullable = false)
    private String eventType;
    @Column( nullable = false)
    private String payload;


    // the two fields below will be set automatically to PENDING when a new event is created
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventStatus status=EventStatus.PENDING;
    @CreationTimestamp
    private LocalDateTime createdAt;


    // the three field below will be updated when the event is processed (either successfully or with failure)

    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    @Column(name = "retry_count")
    private Integer retryCount=0;
    @Column(name = "error_message")
    private String errorMessage;




}

/*
id (UUID)
aggregate_id (UUID) - e.g., orderId
event_type (string) - e.g., "ORDER_CREATED"
payload (JSON) - full event data
status (enum: PENDING, PROCESSED, FAILED)
created_at (timestamp)
processed_at (timestamp)
retry_count (int)
error_message (string
 */