package com.wassimlagnaoui.ecommerce.user_service.Exceptions;

public class InvalidLoginException extends RuntimeException {
    public InvalidLoginException(String message) {
        super(message);
    }
}
