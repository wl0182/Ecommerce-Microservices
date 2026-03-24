package com.wassimlagnaoui.ecommerce.Cart_Service.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<HashMap<String,String>> handleCartNotFoundException(CartNotFoundException ex) {
        HashMap<String,String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        errorResponse.put("status", "404");
        errorResponse.put("message", "Cart not found");
        errorResponse.put("suggestion", "Please check the user ID and try again.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<HashMap<String,String>> handleProductNotFoundException(ProductNotFoundException ex) {
        HashMap<String,String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        errorResponse.put("status", "404");
        errorResponse.put("message", "Product not found from Product Service");
        errorResponse.put("suggestion", "Please check the product IDs in the cart and try again.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // handle kafka publisher exception
    @ExceptionHandler(KafkaPublisherException.class)
    public  ResponseEntity<HashMap<String,String>>handleKafkaPublisherException(KafkaPublisherException ex) {
        HashMap<String,String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        errorResponse.put("status", "500");
        errorResponse.put("message", "Failed to publish event to Kafka");
        errorResponse.put("suggestion", "Please check the Kafka configuration and try again.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

    // handle order service down exception
    @ExceptionHandler(OrderServiceDownException.class)
    public  ResponseEntity<HashMap<String,String>>handleOrderServiceDownException(OrderServiceDownException ex) {
        HashMap<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        errorResponse.put("status", HttpStatus.SERVICE_UNAVAILABLE.toString());
        errorResponse.put("message", "Order Service is currently unavailable");
        errorResponse.put("suggestion", "Please try again later.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);

    }

    // handle product list empty exception
    @ExceptionHandler(ProductListEmptyException.class)
    public  ResponseEntity<HashMap<String,String>>handleProductListEmptyException(ProductListEmptyException ex) {
        HashMap<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        errorResponse.put("status", HttpStatus.NOT_FOUND.toString());
        errorResponse.put("message", "Product list in the cart is empty");
        errorResponse.put("suggestion", "Please add products to the cart before checkout.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // handle product service unavailable exception
    @ExceptionHandler(ProductServiceUnavailble.class)
    public  ResponseEntity<HashMap<String,String>>handleProductServiceUnavailble(ProductServiceUnavailble ex) {
        HashMap<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        errorResponse.put("status", HttpStatus.SERVICE_UNAVAILABLE.toString());
        errorResponse.put("message", "Product Service is currently unavailable");
        errorResponse.put("suggestion", "Please try again later.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

    // handle quantity unavailable exception
    @ExceptionHandler(QuantityUnavailable.class)
    public  ResponseEntity<HashMap<String,String>>handleQuantityUnavailable(QuantityUnavailable ex) {
        HashMap<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.toString());
        errorResponse.put("message", "Requested quantity is unavailable in inventory");
        errorResponse.put("suggestion", "Please check the available inventory for the products and try again.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // handle IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public  ResponseEntity<HashMap<String,String>>handleIllegalArgumentException(IllegalArgumentException ex) {
        HashMap<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.toString());
        errorResponse.put("message", "Invalid input provided");
        errorResponse.put("suggestion", "Please check the input data and try again.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }



}
