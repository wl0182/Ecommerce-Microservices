package com.wassimlagnaoui.ecommerce.Cart_Service.Exception;

public class OrderServiceDownException extends RuntimeException {
    public OrderServiceDownException(String message) {
        super(message);
    }
}
