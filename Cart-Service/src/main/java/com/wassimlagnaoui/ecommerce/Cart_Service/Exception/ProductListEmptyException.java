package com.wassimlagnaoui.ecommerce.Cart_Service.Exception;

public class ProductListEmptyException extends RuntimeException {
    public ProductListEmptyException(String message) {
        super(message);
    }
}
