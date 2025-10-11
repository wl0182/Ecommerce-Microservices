package com.wassimlagnaoui.ecommerce.product_service.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDTO {
    Long transactionId;
    Long productId;
    String type; // ADD or REMOVE
    String description;
}
