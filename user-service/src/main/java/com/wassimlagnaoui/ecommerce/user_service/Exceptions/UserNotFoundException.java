package com.wassimlagnaoui.ecommerce.user_service.Exceptions;



public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
