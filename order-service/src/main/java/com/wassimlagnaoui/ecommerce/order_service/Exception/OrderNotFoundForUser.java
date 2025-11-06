package com.wassimlagnaoui.ecommerce.order_service.Exception;

public class OrderNotFoundForUser extends RuntimeException {
    public OrderNotFoundForUser(String message) {
        super(message);
    }
}
