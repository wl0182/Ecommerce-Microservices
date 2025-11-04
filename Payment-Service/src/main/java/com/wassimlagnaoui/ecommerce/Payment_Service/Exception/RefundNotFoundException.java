package com.wassimlagnaoui.ecommerce.Payment_Service.Exception;

public class RefundNotFoundException extends RuntimeException {
    public RefundNotFoundException(String message) {
        super(message);
    }
}
