package com.wassimlagnaoui.ecommerce.Cart_Service.Exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
