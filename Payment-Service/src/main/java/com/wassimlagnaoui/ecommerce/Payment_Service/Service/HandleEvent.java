package com.wassimlagnaoui.ecommerce.Payment_Service.Service;


import com.wassimlagnaoui.common_events.Events.OrderService.OrderCreateEvent;
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

import java.time.LocalDateTime;

@Service
public class HandleEvent {
    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;

    @Autowired
    private KafkaPublisher kafkaPublisher;

    public HandleEvent(PaymentRepository paymentRepository, RefundRepository refundRepository) {
        this.paymentRepository = paymentRepository;
        this.refundRepository = refundRepository;
    }

    // handle order created event
    @KafkaListener(topics = KafkaTopics.ORDER_CREATED, groupId = KafkaGroupIds.PAYMENT_SERVICE_GROUP)
    public void handleOrderCreatedEvent(OrderCreateEvent orderCreateEvent) {
        // simulate a payment with a 95% success rate
        ProcessPaymentResponse paymentResponse;
        double random = Math.random();
        if (random < 0.95) {
            paymentResponse = processSuccessFullPayment(orderCreateEvent);
        } else {
             paymentResponse = processFailedPayment(orderCreateEvent);
        }

    }


    public ProcessPaymentResponse processFailedPayment(OrderCreateEvent orderCreateEvent) {
        Payment payment = new Payment();
        payment.setOrderId(Long.valueOf(orderCreateEvent.getOrderId()));
        payment.setAmount(orderCreateEvent.getTotalAmount());
        payment.setMethod(PaymentMethod.valueOf(orderCreateEvent.getPaymentMethod()));
        payment.setStatus(PaymentStatus.FAILED);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);

        // publish payment failed event

        return ProcessPaymentResponse.builder()
                .id(savedPayment.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getMethod().name())
                .status(payment.getStatus().name())
                .createdAt(payment.getCreatedAt().toString())
                .build();
    }

    public ProcessPaymentResponse processSuccessFullPayment(OrderCreateEvent orderCreateEvent) {

        Payment payment = new Payment();
        payment.setOrderId(Long.valueOf(orderCreateEvent.getOrderId()));
        payment.setAmount(orderCreateEvent.getTotalAmount());
        payment.setMethod(PaymentMethod.valueOf(orderCreateEvent.getPaymentMethod()));
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);

        // publish payment success event
        PaymentProcessed paymentProcessed = new PaymentProcessed();
        paymentProcessed.setPaymentId(savedPayment.getId().toString());
        paymentProcessed.setOrderId(payment.getOrderId());
        paymentProcessed.setUserId(orderCreateEvent.getUserId().toString());
        paymentProcessed.setAmount(payment.getAmount());
        paymentProcessed.setStatus(payment.getStatus().name());
        paymentProcessed.setPaymentMethod(payment.getMethod().name());
        paymentProcessed.setCreatedAt(java.time.Instant.now());
        kafkaPublisher.publish(KafkaTopics.PAYMENT_PROCESSED, paymentProcessed);


        return ProcessPaymentResponse.builder()
                .id(savedPayment.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getMethod().name())
                .status(payment.getStatus().name())
                .createdAt(payment.getCreatedAt().toString())
                .build();
    }




}
