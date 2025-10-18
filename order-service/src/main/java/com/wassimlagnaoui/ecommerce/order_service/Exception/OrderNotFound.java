package com.wassimlagnaoui.ecommerce.order_service.Exception;

public class OrderNotFound extends RuntimeException {
    public OrderNotFound(String message) {
        super(message);
    }
}
