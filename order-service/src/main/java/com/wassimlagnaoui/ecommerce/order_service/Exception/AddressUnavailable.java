package com.wassimlagnaoui.ecommerce.order_service.Exception;

public class AddressUnavailable extends RuntimeException {
    public AddressUnavailable(String message) {
        super(message);
    }
}
