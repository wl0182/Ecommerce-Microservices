package com.wassimlagnaoui.ecommerce.Notification_Service.Domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "templates")
public class Template {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private NotificationType type; // ORDER_CONFIRMED, PAYMENT_SUCCESS, DELIVERED
    private String subject;
    private String body;
}// Template → { id, type, subject, body }
