package com.wassimlagnaoui.ecommerce.product_service.Exception;

import org.springframework.web.bind.annotation.ExceptionHandler;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(String message) {
        super(message);
    }
}
