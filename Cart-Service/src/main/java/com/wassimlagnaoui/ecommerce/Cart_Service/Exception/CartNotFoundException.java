package com.wassimlagnaoui.ecommerce.Cart_Service.Exception;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(String message) {
        super(message);
    }
}
