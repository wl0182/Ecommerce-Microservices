package com.wassimlagnaoui.ecommerce.order_service.Exception;

public class ProductServiceError extends RuntimeException {
    public ProductServiceError(String message) {
        super(message);
    }
}
