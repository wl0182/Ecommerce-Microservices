package com.wassimlagnaoui.ecommerce.Cart_Service.Exception;

public class KafkaPublisherException extends RuntimeException {
    public KafkaPublisherException(String message) {
        super(message);
    }
}
