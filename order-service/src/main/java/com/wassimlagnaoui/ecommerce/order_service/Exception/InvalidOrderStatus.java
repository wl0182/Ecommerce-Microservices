package com.wassimlagnaoui.ecommerce.order_service.Exception;

public class InvalidOrderStatus extends RuntimeException {
    public InvalidOrderStatus(String message) {
        super(message);
    }
}
