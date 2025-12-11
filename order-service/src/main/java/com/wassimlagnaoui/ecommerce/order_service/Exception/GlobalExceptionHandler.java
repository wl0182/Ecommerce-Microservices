package com.wassimlagnaoui.ecommerce.order_service.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(OrderNotFound.class)
    public ResponseEntity<HashMap<String,String>>handleOrderNotFoundException(OrderNotFound ex) {
        HashMap<String,String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("status", "404");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        response.put("info", "Order Not Found, please check the order ID and try again.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(OrderNotFoundForUser.class)
    public ResponseEntity<HashMap<String,String>> handleOrderNotFoundForUserException(OrderNotFoundForUser ex) {
        HashMap<String,String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("status", "404");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        response.put("info", "No orders found for the specified user ID, please verify and try again.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // handle invalid order status exception
    @ExceptionHandler(InvalidOrderStatus.class)
    public ResponseEntity<HashMap<String,String>> handleInvalidOrderStatusException(InvalidOrderStatus ex) {
        HashMap<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.toString());
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        response.put("info", "The provided order status is invalid, please check and try again.");
        return ResponseEntity.badRequest().body(response);
    }


}
