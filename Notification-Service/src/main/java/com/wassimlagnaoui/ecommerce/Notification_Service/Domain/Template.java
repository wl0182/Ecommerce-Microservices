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
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String subject;


    @Column(columnDefinition = "TEXT")
    private String body; // html content with placeholders for dynamic data
}





