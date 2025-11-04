package com.wassimlagnaoui.ecommerce.Payment_Service.Exception;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(String message) {
        super(message);
    }
}
