package com.wassimlagnaoui.ecommerce.order_service.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HashMap<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("error", "Validation failed for the request body.");
        response.put("status", HttpStatus.BAD_REQUEST.toString());
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        response.put("info", "Please check the request body for validation errors and try again.");
        response.put("validationErrors", ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList());
        return ResponseEntity.badRequest().body(response);

    }

    @ExceptionHandler(ProductServiceError.class)
    public ResponseEntity<HashMap<String, String>> handleProductServiceError(ProductServiceError ex) {
        HashMap<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.toString());
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        response.put("info", "An error occurred while communicating with the Product Service. Please try again later.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    // handle OrderItemsEmptyException
    @ExceptionHandler(OrderItemsListEmpty.class)
    public ResponseEntity<HashMap<String, String>> handleOrderItemsEmptyException(OrderItemsListEmpty ex) {
        HashMap<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.toString());
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        response.put("info", "The order must contain at least one item. Please add items to the order and try again.");
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<HashMap<String, String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        HashMap<String, String> response = new HashMap<>();
        response.put("error", "Malformed JSON request: " + ex.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.toString());
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        response.put("info", "The request body contains malformed JSON. Please check the syntax and try again.");
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AddressUnavailable.class)
    public ResponseEntity<HashMap<String, String>> handleAddressUnavailableException(AddressUnavailable ex) {
        HashMap<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("status", HttpStatus.NOT_FOUND.toString());
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        response.put("info", "The specified address is currently unavailable. Please verify the address and try again later.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserServiceError.class)
    public ResponseEntity<HashMap<String, String>> handleUserServiceError(UserServiceError ex){
        HashMap<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.toString());
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        response.put("info", "An error occurred while communicating with the User Service. Please try again later.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }


}
