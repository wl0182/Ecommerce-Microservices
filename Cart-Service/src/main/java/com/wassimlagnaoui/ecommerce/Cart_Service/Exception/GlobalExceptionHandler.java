package com.wassimlagnaoui.ecommerce.Cart_Service.Exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CartNotFoundException.class)
    public HashMap<String,String> handleCartNotFoundException(CartNotFoundException ex) {
        HashMap<String,String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        errorResponse.put("status", "404");
        errorResponse.put("message", "Cart not found");
        errorResponse.put("suggestion", "Please check the user ID and try again.");
        return errorResponse;
    }
}
