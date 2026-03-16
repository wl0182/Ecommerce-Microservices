package com.wassimlagnaoui.ecommerce.Payment_Service.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {

    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private String status;
    private String createdAt;

}
// { id, orderId, amount, status, createdAt }
