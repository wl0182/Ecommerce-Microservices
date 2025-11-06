package com.wassimlagnaoui.ecommerce.order_service.Exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(OrderNotFound.class)
    public HashMap<String,String> handleOrderNotFoundException(OrderNotFound ex) {
        HashMap<String,String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("status", "404");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        response.put("info", "Order Not Found, please check the order ID and try again.");
        return response;
    }

    @ExceptionHandler(OrderNotFoundForUser.class)
    public HashMap<String,String> handleOrderNotFoundForUserException(OrderNotFoundForUser ex) {
        HashMap<String,String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("status", "404");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        response.put("info", "No orders found for the specified user ID, please verify and try again.");
        return response;
    }

}
