package com.wassimlagnaoui.ecommerce.Payment_Service.Exception;

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
}
