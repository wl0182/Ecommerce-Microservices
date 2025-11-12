package com.wassimlagnaoui.ecommerce.product_service.Exception;

public class KafkaEventNotSentException extends RuntimeException {
    public KafkaEventNotSentException(String message) {
        super(message);
    }
}
