package com.wassimlagnaoui.ecommerce.product_service.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<HashMap<String, String>> handleProductNotFoundException(ProductNotFoundException ex) {
        HashMap<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // InsufficientStockException handler
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<HashMap<String, String>> handleInsufficientStockException(InsufficientStockException ex) {
        HashMap<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // CategoryNotFoundException handler
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<HashMap<String, String>> handleCategoryNotFoundException(CategoryNotFoundException ex) {
        HashMap<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        errorResponse.put("type", "Category Not Found");
        errorResponse.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

    }

    // kafka event not sent exception handler
    @ExceptionHandler(KafkaEventNotSentException.class)
    public ResponseEntity<HashMap<String, String>> handleKafkaEventNotSentException(KafkaEventNotSentException ex) {
        HashMap<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        errorResponse.put("type", "Kafka Event Not Sent");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("suggestion", "Please check Kafka server and connectivity.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

}
