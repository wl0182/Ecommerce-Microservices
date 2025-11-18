package com.wassimlagnaoui.ecommerce.Payment_Service.Service;

import com.wassimlagnaoui.common_events.Events.PaymentService.PaymentRefunded;
import com.wassimlagnaoui.common_events.KafkaTopics;
import com.wassimlagnaoui.ecommerce.Payment_Service.DTO.IssueRefundRequest;
import com.wassimlagnaoui.ecommerce.Payment_Service.DTO.IssueRefundResponse;
import com.wassimlagnaoui.ecommerce.Payment_Service.DTO.PaymentDTO;
import com.wassimlagnaoui.ecommerce.Payment_Service.Domain.Payment;
import com.wassimlagnaoui.ecommerce.Payment_Service.Domain.PaymentStatus;
import com.wassimlagnaoui.ecommerce.Payment_Service.Domain.Refund;
import com.wassimlagnaoui.ecommerce.Payment_Service.Exception.PaymentNotFoundException;
import com.wassimlagnaoui.ecommerce.Payment_Service.Repository.PaymentRepository;
import com.wassimlagnaoui.ecommerce.Payment_Service.Repository.RefundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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







}
