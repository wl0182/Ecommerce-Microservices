package com.wassimlagnaoui.ecommerce.Payment_Service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueRefundRequest {
    private Long orderId;
    private String reason;

}

// { orderId, reason }
