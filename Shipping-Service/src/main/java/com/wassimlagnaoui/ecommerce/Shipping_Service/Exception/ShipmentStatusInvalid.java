package com.wassimlagnaoui.ecommerce.Shipping_Service.Exception;

public class ShipmentStatusInvalid extends IllegalArgumentException {
    public ShipmentStatusInvalid(String message) {
        super(message);
    }
}
