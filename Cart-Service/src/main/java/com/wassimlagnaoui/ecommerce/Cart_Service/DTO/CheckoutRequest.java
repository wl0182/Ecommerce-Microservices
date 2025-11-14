package com.wassimlagnaoui.ecommerce.Cart_Service.DTO;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutRequest {

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // to be validated against PaymentMethod enum in Cart-Service
    private Long addressId;

}// { paymentMethod, addressId }