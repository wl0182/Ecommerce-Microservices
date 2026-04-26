package com.wassimlagnaoui.ecommerce.Payment_Service.Exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.protocol.HTTP;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(PaymentNotFoundException.class)
    public HashMap<String, String> handlePaymentNotFoundException(PaymentNotFoundException ex) {
        HashMap<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("status", "404");
        response.put("type", "Payment Not Found");
        response.put("solution", "Please check the payment ID and try again.");
        return response;
    }

    // handle RefundNotFoundException
    @ExceptionHandler(RefundNotFoundException.class)
    public HashMap<String, String> handleRefundNotFoundException(RefundNotFoundException ex) {
        HashMap<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("status", "404");
        response.put("type", "Refund Not Found");
        response.put("solution", "Please check the refund ID and try again.");
        return response;
    }

    // handle MthodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public HashMap<String, Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("error", "Validation failed for the request body.");
        response.put("status", "400");
        response.put("type", "Bad Request");
        response.put("solution", "Please check the request body for validation errors and try again.");
        response.put("validationErrors", ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList());
        return response;
    }

    // Json parse exception
    @ExceptionHandler(JsonProcessingException.class)
    public HashMap<String, String> handleJsonProcessingException(JsonProcessingException ex) {
        HashMap<String, String> response = new HashMap<>();
        response.put("error", "Failed to process JSON data.");
        response.put("status", HttpStatus.BAD_REQUEST.toString());
        response.put("type", "Bad Request");
        response.put("solution", "Please check the JSON data for syntax errors and try again.");
        return response;
    }

    // handle SerializationException
    @ExceptionHandler(SerialiazationException.class)
    public HashMap<String, String> handleSerializationException(SerialiazationException ex) {
        HashMap<String, String> response = new HashMap<>();
        response.put("error", "Failed to serialize data.");
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.toString());
        response.put("type", "Internal Server Error");
        response.put("solution", "Please check the server logs for more details and try again.");
        return response;
    }
}
