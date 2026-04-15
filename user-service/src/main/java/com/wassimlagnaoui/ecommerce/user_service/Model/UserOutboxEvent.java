package com.wassimlagnaoui.ecommerce.user_service.Model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_outbox_events")
public class UserOutboxEvent {
    @Id
    private UUID id; // UUID
    @Enumerated(EnumType.STRING)
    private EventStatus status;

    private String payload; // JSON string

    @Column(name = "aggregate_id")
    private String aggregateId;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "error_message")
    private String errorMessage;

}

/*
columns should match the following:
 id UUID PRIMARY KEY,
    aggregate_id VARCHAR(255),
    event_type VARCHAR(255),
    payload TEXT,
    status VARCHAR(50),
    created_at TIMESTAMP,
    processed_at TIMESTAMP,
    retry_count INTEGER DEFAULT 0,
    error_message TEXT
 */

