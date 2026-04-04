package com.wassimlagnaoui.ecommerce.Payment_Service.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueRefundRequest {
    @NotNull(message = "Order ID cannot be null")
    private Long orderId;
    private String reason;

}

// { orderId, reason }
