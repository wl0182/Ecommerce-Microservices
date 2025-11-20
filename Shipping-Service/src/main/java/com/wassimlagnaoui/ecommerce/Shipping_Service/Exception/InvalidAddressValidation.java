package com.wassimlagnaoui.ecommerce.Shipping_Service.Exception;

public class InvalidAddressValidation extends RuntimeException {
    public InvalidAddressValidation(String message) {
        super(message);
    }
}
