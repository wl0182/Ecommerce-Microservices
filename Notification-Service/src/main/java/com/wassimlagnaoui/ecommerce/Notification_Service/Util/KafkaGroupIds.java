package com.wassimlagnaoui.ecommerce.Notification_Service.Util;

public class KafkaGroupIds {
    private KafkaGroupIds() {
        // Prevent instantiation
    }



    // ----------------- SERVICE GROUP IDs -----------------
    public static final String USER_SERVICE_GROUP = "user-service-group";
    public static final String PRODUCT_SERVICE_GROUP = "product-service-group";
    public static final String CART_SERVICE_GROUP = "cart-service-group";
    public static final String ORDER_SERVICE_GROUP = "order-service-group";
    public static final String PAYMENT_SERVICE_GROUP = "payment-service-group";
    public static final String SHIPPING_SERVICE_GROUP = "shipping-service-group";
    public static final String NOTIFICATION_SERVICE_GROUP = "notification-service-group";
}
