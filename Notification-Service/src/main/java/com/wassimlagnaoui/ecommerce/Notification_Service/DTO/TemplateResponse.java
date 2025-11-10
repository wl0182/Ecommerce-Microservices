package com.wassimlagnaoui.ecommerce.Notification_Service.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemplateResponse {
    private Long id;
    private String type;
    private String subject;
    private String body;
}
