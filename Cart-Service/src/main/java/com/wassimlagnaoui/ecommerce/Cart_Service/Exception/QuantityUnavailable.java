package com.wassimlagnaoui.ecommerce.Cart_Service.Exception;

public class QuantityUnavailable extends RuntimeException {
    public QuantityUnavailable(String message) {
        super(message);
    }
}
