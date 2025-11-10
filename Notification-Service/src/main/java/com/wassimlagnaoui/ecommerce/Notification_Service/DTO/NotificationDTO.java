package com.wassimlagnaoui.ecommerce.Notification_Service.DTO;

import jakarta.annotation.security.DenyAll;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private Long id;
    private String type;
    private String message;
    private String status;
    private LocalDateTime createdAt;
}

// { id, type, message, status, createdAt }
