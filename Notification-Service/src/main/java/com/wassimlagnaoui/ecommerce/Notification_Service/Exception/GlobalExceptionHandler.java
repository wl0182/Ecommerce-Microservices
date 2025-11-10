package com.wassimlagnaoui.ecommerce.Notification_Service.Exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;

@ControllerAdvice
public class GlobalExceptionHandler {

    // NotificationNotFoundException handler
    @ExceptionHandler(NotificationNotFound.class)
    public ResponseEntity<HashMap<String,String>> handleNotificationNotFoundException(NotificationNotFound ex) {
        HashMap<String,String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("status", "404");
        response.put("type", "Notification Not Found");
        response.put("solution", "Please check the notification ID and try again.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // handle TemplateNotFound exception
    @ExceptionHandler(TemplateNotFound.class)
    public ResponseEntity<HashMap<String,String> >handleTemplateNotFoundException(TemplateNotFound ex) {
        HashMap<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("status", "404");
        response.put("type", "Template Not Found");
        response.put("solution", "Please check the template ID and try again.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // handle invalid NotificationType exception
    @ExceptionHandler(InvalidNotificationType.class)
    public ResponseEntity<HashMap<String,String>> handleInvalidNotificationTypeException(InvalidNotificationType ex) {
        HashMap<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("status", "400");
        response.put("type", "Invalid Notification Type");
        response.put("solution", "Please provide a valid notification type.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
