package com.wassimlagnaoui.ecommerce.Payment_Service.Service;


import com.wassimlagnaoui.common_events.Events.OrderService.OrderCreateEvent;
import com.wassimlagnaoui.common_events.Events.PaymentService.PaymentFailed;
import com.wassimlagnaoui.common_events.Events.PaymentService.PaymentProcessed;
import com.wassimlagnaoui.common_events.KafkaGroupIds;
import com.wassimlagnaoui.common_events.KafkaTopics;
import com.wassimlagnaoui.ecommerce.Payment_Service.DTO.ProcessPaymentResponse;
import com.wassimlagnaoui.ecommerce.Payment_Service.Domain.Payment;
import com.wassimlagnaoui.ecommerce.Payment_Service.Domain.PaymentMethod;
import com.wassimlagnaoui.ecommerce.Payment_Service.Domain.PaymentStatus;
import com.wassimlagnaoui.ecommerce.Payment_Service.Repository.PaymentRepository;
import com.wassimlagnaoui.ecommerce.Payment_Service.Repository.RefundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class HandleEvent {

    @Autowired
    private PaymentService paymentService ;



    // handle order created event
    @KafkaListener(topics = KafkaTopics.ORDER_CREATED, groupId = KafkaGroupIds.PAYMENT_SERVICE_GROUP)
    @Transactional
    public void handleOrderCreatedEvent(OrderCreateEvent orderCreateEvent) {
        // simulate a payment with a 95% success rate
        ProcessPaymentResponse paymentResponse;
        double random = Math.random();
        if (random < 0.95) {
            paymentResponse = paymentService.processSuccessFullPayment(orderCreateEvent);
        } else {
            paymentResponse = paymentService.processFailedPayment(orderCreateEvent);
        }

    }




}