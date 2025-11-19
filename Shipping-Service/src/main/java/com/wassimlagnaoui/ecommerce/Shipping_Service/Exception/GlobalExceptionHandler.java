package com.wassimlagnaoui.ecommerce.Shipping_Service.Exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
