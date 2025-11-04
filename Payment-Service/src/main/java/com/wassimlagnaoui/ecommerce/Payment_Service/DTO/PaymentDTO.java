package com.wassimlagnaoui.ecommerce.Payment_Service.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {

    private Long id;
    private Long orderId;
    private Double amount;
    private String status;
    private String createdAt;

}
// { id, orderId, amount, status, createdAt }
