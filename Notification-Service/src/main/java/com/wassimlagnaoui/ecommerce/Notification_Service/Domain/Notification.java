package com.wassimlagnaoui.ecommerce.Notification_Service.Domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long userId;

    @Enumerated(EnumType.STRING)
    private NotificationType type; // ORDER_CONFIRMED, PAYMENT_SUCCESS, DELIVERED

    @Enumerated(EnumType.STRING)
    private NotificationChannel channel; // EMAIL, SMS

    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status; // SENT, FAILED

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


}


/*
 * Notification → { id, userId, type[ORDER_CONFIRMED/PAYMENT_SUCCESS/DELIVERED], channel[EMAIL/SMS], message, status[SENT/FAILED], createdAt }
 * Template → { id, type, subject, body }
 */