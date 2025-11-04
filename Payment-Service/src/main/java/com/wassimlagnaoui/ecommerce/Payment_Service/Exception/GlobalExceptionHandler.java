package com.wassimlagnaoui.ecommerce.Payment_Service.Exception;

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
}
