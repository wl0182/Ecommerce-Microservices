package com.wassimlagnaoui.common_events;


public final class KafkaTopics {

    private KafkaTopics() {
        // Prevent instantiation
    }

    public static final String TOPIC_PRODUCT_TEST = "Dummy-Test-Topic";

    // ----------------- USER SERVICE -----------------
    public static final String USER_REGISTERED = "user-registered"; // Produced by User-Service; Consumed by Notification-Service

    // ----------------- PRODUCT SERVICE -----------------
    public static final String PRODUCT_UPDATED = "product-updated"; // Produced by Product-Service; Consumed by Cart & Order Services

    // ----------------- CART SERVICE -----------------
    public static final String CART_CLEARED = "cart-cleared"; // Produced by Cart-Service; Internal event

    // ----------------- ORDER SERVICE -----------------
    public static final String ORDER_CREATED = "order-created";   // Produced by Order-Service; Consumed by Payment & Notification Services
    public static final String ORDER_CANCELLED = "order-cancelled"; // Produced by Order-Service; Consumed by Product, Payment, and Notification Services
    public static final String ORDER_PAID = "order-paid";         // Produced by Payment-Service; Consumed by Shipping, Order, and Notification Services

    // ----------------- PAYMENT SERVICE -----------------
    public static final String PAYMENT_PROCESSED = "payment-processed"; // Produced by Payment-Service; Consumed by Order & Notification Services
    public static final String PAYMENT_REFUNDED = "payment-refunded";// Produced by Payment-Service; Consumed by Order & Notification Services
    public static final String PAYMENT_FAILED = "payment-failed";         // Produced by Payment-Service; Consumed by Order & Notification Services

    // ----------------- SHIPPING SERVICE -----------------
    public static final String SHIPMENT_CREATED = "shipment-created"; // Produced by Shipping-Service; Consumed by Notification-Service
    public static final String SHIPMENT_UPDATED = "shipment-updated";

}
