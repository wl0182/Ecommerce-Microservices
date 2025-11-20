package com.wassimlagnaoui.ecommerce.user_service.Exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HashMap<String,String>> handleUserNotFoundException(UserNotFoundException ex) {
        HashMap<String,String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("status", "404");
        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(InvalidAddressValidation.class)
    public ResponseEntity<HashMap<String,String>> handleInvalidAddressValidation(InvalidAddressValidation ex) {
        HashMap<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
