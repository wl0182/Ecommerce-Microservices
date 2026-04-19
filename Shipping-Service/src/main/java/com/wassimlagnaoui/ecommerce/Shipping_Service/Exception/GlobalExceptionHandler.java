package com.wassimlagnaoui.ecommerce.Shipping_Service.Exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ShipmentNotFoundException.class)
    public ResponseEntity<HashMap<String, String>> handleShipmentNotFoundException(ShipmentNotFoundException ex) {
        HashMap<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        errorResponse.put("status", HttpStatus.NOT_FOUND.toString());
        errorResponse.put("timestamp", String.valueOf(System.currentTimeMillis()));
        errorResponse.put("Info", "Shipment not found in the system");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ShipmentStatusInvalid.class)
    public ResponseEntity<HashMap<String, String>> handleShipmentStatusInvalidException(ShipmentStatusInvalid ex) {
        HashMap<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.toString());
        errorResponse.put("timestamp", String.valueOf(System.currentTimeMillis()));
        errorResponse.put("Info", "The provided shipment status is invalid");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // InvalidAddressValidation exception handler
    @ExceptionHandler(InvalidAddressValidation.class)
    public ResponseEntity<HashMap<String, String>> handleInvalidAddressValidationException(InvalidAddressValidation ex) {
        HashMap<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        errorResponse.put("status", HttpStatus.SERVICE_UNAVAILABLE.toString());
        errorResponse.put("timestamp", String.valueOf(System.currentTimeMillis()));
        errorResponse.put("Info", "The provided address is invalid");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

    // handle MethodArgumentNotValidException for validation errors
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
}
