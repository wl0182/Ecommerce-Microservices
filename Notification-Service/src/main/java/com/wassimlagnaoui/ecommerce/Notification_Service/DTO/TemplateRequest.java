package com.wassimlagnaoui.ecommerce.Notification_Service.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateRequest {
    @NonNull
    private String type;
    @NotBlank(message = "Subject cannot be blank")
    private String subject;
    @NotBlank(message = "Body cannot be blank")
    private String body;
}
