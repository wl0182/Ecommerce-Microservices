package com.wassimlagnaoui.ecommerce.Payment_Service.Service;

import com.wassimlagnaoui.ecommerce.Payment_Service.DTO.PaymentDTO;
import com.wassimlagnaoui.ecommerce.Payment_Service.Domain.Payment;
import com.wassimlagnaoui.ecommerce.Payment_Service.Exception.PaymentNotFoundException;
import com.wassimlagnaoui.ecommerce.Payment_Service.Repository.PaymentRepository;
import com.wassimlagnaoui.ecommerce.Payment_Service.Repository.RefundRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;

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







}
