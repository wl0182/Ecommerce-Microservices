package com.wassimlagnaoui.ecommerce.Payment_Service.Service;

import com.wassimlagnaoui.common_events.Events.OrderService.OrderCreateEvent;
import com.wassimlagnaoui.common_events.Events.PaymentService.PaymentFailed;
import com.wassimlagnaoui.common_events.Events.PaymentService.PaymentProcessed;
import com.wassimlagnaoui.common_events.Events.PaymentService.PaymentRefunded;
import com.wassimlagnaoui.common_events.KafkaTopics;
import com.wassimlagnaoui.ecommerce.Payment_Service.DTO.IssueRefundRequest;
import com.wassimlagnaoui.ecommerce.Payment_Service.DTO.IssueRefundResponse;
import com.wassimlagnaoui.ecommerce.Payment_Service.DTO.PaymentDTO;
import com.wassimlagnaoui.ecommerce.Payment_Service.DTO.ProcessPaymentResponse;
import com.wassimlagnaoui.ecommerce.Payment_Service.Domain.Payment;
import com.wassimlagnaoui.ecommerce.Payment_Service.Domain.PaymentMethod;
import com.wassimlagnaoui.ecommerce.Payment_Service.Domain.PaymentStatus;
import com.wassimlagnaoui.ecommerce.Payment_Service.Domain.Refund;
import com.wassimlagnaoui.ecommerce.Payment_Service.Exception.PaymentNotFoundException;
import com.wassimlagnaoui.ecommerce.Payment_Service.Repository.PaymentRepository;
import com.wassimlagnaoui.ecommerce.Payment_Service.Repository.RefundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;

    @Autowired
    private KafkaPublisher kafkaPublisher; // to publish events to Kafka

    public PaymentService(PaymentRepository paymentRepository, RefundRepository refundRepository) {
        this.paymentRepository = paymentRepository;
        this.refundRepository = refundRepository;
    }

    // get payment by paymentId
    public PaymentDTO getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + paymentId));

        return PaymentDTO.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(payment.getStatus().toString())
                .createdAt(payment.getCreatedAt().toString())
                .build();

    }

    // Get Payments for order
    public List<PaymentDTO> getPaymentsByOrderId(Long orderId) {
        List<Payment> payments = paymentRepository.findByOrderId((orderId));

        if (payments.isEmpty()) {
            throw new PaymentNotFoundException("No payments found for order id: " + orderId);
        }


        return payments.stream().map(payment -> PaymentDTO.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(payment.getStatus().toString())
                .createdAt(payment.getCreatedAt().toString())
                .build()).toList();
    }



    @Transactional
    public IssueRefundResponse issueRefund(IssueRefundRequest request) {
       // find payment by orderId
        Payment payment = paymentRepository.findByOrderId(request.getOrderId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new PaymentNotFoundException("No payment found for order id: " + request.getOrderId()));

        // create Refund entity and save it // Refund → { id, paymentId, reason, amount, createdAt }
        Refund refund = new Refund();
        refund.setPaymentId(payment.getId());
        refund.setReason(request.getReason());
        refund.setAmount(payment.getAmount());
        refund.setCreatedAt(LocalDateTime.now());
        refundRepository.save(refund);

        // publish refund event to Kafka topic "Payment-refunded"  // { paymentId, orderId, amount, refundedAt, reason }
        PaymentRefunded refundEvent = PaymentRefunded.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .amount(BigDecimal.valueOf(payment.getAmount()))
                .refundedAt(java.time.Instant.now())
                .reason(request.getReason())
                .build();

        kafkaPublisher.publish(KafkaTopics.PAYMENT_REFUNDED, refundEvent);



        return IssueRefundResponse.builder()
                .id(refund.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(PaymentStatus.REFUNDED.name())
                .refundedAt(refund.getCreatedAt())
                .build(); // { id, orderId, amount, status:"REFUNDED", refundedAt }
    }


    @Transactional
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


        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                kafkaPublisher.publish(KafkaTopics.PAYMENT_PROCESSED, paymentProcessed);
            }
        });



        return ProcessPaymentResponse.builder()
                .id(savedPayment.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getMethod().name())
                .status(payment.getStatus().name())
                .createdAt(payment.getCreatedAt().toString())
                .build();
    }

    @Transactional
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
        PaymentFailed paymentFailed = new PaymentFailed();
        paymentFailed.setPaymentId(savedPayment.getId().toString());
        paymentFailed.setOrderId(payment.getOrderId());
        paymentFailed.setUserId(orderCreateEvent.getUserId().toString());
        paymentFailed.setAmount(BigDecimal.valueOf(payment.getAmount()));
        paymentFailed.setFailedAt(java.time.Instant.now());

        // transaction manager after commit implementation


        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                kafkaPublisher.publish(KafkaTopics.PAYMENT_FAILED, paymentFailed);
            }
        });


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









