package com.wassimlagnaoui.ecommerce.Payment_Service.Controller;

import com.wassimlagnaoui.ecommerce.Payment_Service.DTO.PaymentDTO;
import com.wassimlagnaoui.ecommerce.Payment_Service.Service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // get payment by id
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long paymentId) {
        PaymentDTO paymentDTO = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(paymentDTO);
    }

    // Get Payment by Order Id
    @GetMapping("/order/{orderId}")
    public ResponseEntity<java.util.List<PaymentDTO>> getPaymentsByOrderId(@PathVariable Long orderId) {
        java.util.List<PaymentDTO> paymentDTOs = paymentService.getPaymentsByOrderId(orderId);
        return ResponseEntity.ok(paymentDTOs);
    }

}
