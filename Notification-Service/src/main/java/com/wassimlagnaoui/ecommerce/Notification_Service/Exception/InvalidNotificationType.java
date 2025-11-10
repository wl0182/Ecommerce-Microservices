package com.wassimlagnaoui.ecommerce.Notification_Service.Exception;

public class InvalidNotificationType extends IllegalArgumentException {
    public InvalidNotificationType(String message) {
        super(message);
    }
}
