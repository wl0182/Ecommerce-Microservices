package com.wassimlagnaoui.ecommerce.Payment_Service.Controller;

import com.wassimlagnaoui.ecommerce.Payment_Service.DTO.IssueRefundRequest;
import com.wassimlagnaoui.ecommerce.Payment_Service.DTO.IssueRefundResponse;
import com.wassimlagnaoui.ecommerce.Payment_Service.DTO.PaymentDTO;
import com.wassimlagnaoui.ecommerce.Payment_Service.Service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<PaymentDTO>> getPaymentsByOrderId(@PathVariable Long orderId) {
        java.util.List<PaymentDTO> paymentDTOs = paymentService.getPaymentsByOrderId(orderId);
        return ResponseEntity.ok(paymentDTOs);
    }

    // Issue refund for a payment
    @PostMapping("/refund")
    public ResponseEntity<IssueRefundResponse> issueRefund(IssueRefundRequest request) {
        IssueRefundResponse response = paymentService.issueRefund(request);
        return ResponseEntity.ok(response);
    }


}
