package com.wassimlagnaoui.ecommerce.Shipping_Service.Exception;

public class ShipmentNotFoundException extends RuntimeException {
    public ShipmentNotFoundException(String message) {
        super(message);
    }
}
