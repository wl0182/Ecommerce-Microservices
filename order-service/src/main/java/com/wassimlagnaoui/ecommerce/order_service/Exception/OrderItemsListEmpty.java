package com.wassimlagnaoui.ecommerce.order_service.Exception;

public class OrderItemsListEmpty extends RuntimeException {
    public OrderItemsListEmpty(String message) {
        super(message);
    }
}
