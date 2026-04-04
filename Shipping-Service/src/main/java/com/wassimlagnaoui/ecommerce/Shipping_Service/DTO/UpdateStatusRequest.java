package com.wassimlagnaoui.ecommerce.Shipping_Service.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateStatusRequest {
    @NotBlank(message = "Status cannot be blank")
    String status;
}
