package com.wassimlagnaoui.ecommerce.product_service.Exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
