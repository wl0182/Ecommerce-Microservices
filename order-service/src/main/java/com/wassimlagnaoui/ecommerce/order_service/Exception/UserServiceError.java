package com.wassimlagnaoui.ecommerce.order_service.Exception;

public class UserServiceError extends RuntimeException {
    public UserServiceError(String message) {
        super(message);
    }
}
