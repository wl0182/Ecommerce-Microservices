package com.wassimlagnaoui.ecommerce.Payment_Service.Service;


import com.wassimlagnaoui.common_events.Events.OrderService.OrderCreateEvent;
import com.wassimlagnaoui.common_events.KafkaGroupIds;
import com.wassimlagnaoui.common_events.KafkaTopics;
import com.wassimlagnaoui.ecommerce.Payment_Service.DTO.ProcessPaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentEventHandler {

    @Autowired
    private PaymentService paymentService ;



    // handle order created event
    @KafkaListener(topics = KafkaTopics.ORDER_CREATED, groupId = KafkaGroupIds.PAYMENT_SERVICE_GROUP)
    public void handleOrderCreatedEvent(OrderCreateEvent orderCreateEvent) {
        // simulate a payment with a 95% success rate
        log.info("Received OrderCreateEvent for orderId: " + orderCreateEvent.getOrderId() + "Full Object: " + orderCreateEvent.toString());

        ProcessPaymentResponse paymentResponse;
        double random = Math.random();
        if (random < 0.75) {
            paymentResponse = paymentService.processSuccessFullPayment(orderCreateEvent);
        } else {
            paymentResponse = paymentService.processFailedPayment(orderCreateEvent);
        }

    }




}