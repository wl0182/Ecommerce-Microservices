package com.wassimlagnaoui.ecommerce.Cart_Service.DTO;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutRequest {

    @Enumerated(EnumType.STRING)
    @NotBlank(message = "Payment method is required")
    private PaymentMethod paymentMethod; // to be validated against PaymentMethod enum in Cart-Service
    @NotNull(message = "Address ID is required")
    private Long addressId;

}// { paymentMethod, addressId }