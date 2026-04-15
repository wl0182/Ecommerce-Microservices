package com.wassimlagnaoui.ecommerce.user_service.Exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

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

    // handle Invalid Login Exception
    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<HashMap<String,String>> handleInvalidLoginException(InvalidLoginException ex) {
        HashMap<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("status", "401");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        });

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("errors", fieldErrors);
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.badRequest().body(response);
    }

}
