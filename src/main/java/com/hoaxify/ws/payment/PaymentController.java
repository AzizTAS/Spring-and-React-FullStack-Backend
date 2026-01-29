package com.hoaxify.ws.payment;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoaxify.ws.payment.dto.CreatePaymentRequest;
import com.hoaxify.ws.payment.dto.PaymentDTO;
import com.hoaxify.ws.payment.dto.UpdatePaymentStatusRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    PaymentDTO getPayment(@PathVariable long id) {
        return new PaymentDTO(paymentService.getPayment(id));
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("isAuthenticated()")
    PaymentDTO getPaymentByOrderId(@PathVariable long orderId) {
        return new PaymentDTO(paymentService.getPaymentByOrderId(orderId));
    }

    @PostMapping("/order/{orderId}")
    @PreAuthorize("isAuthenticated()")
    PaymentDTO createPayment(@PathVariable long orderId, @Valid @RequestBody CreatePaymentRequest request) {
        return new PaymentDTO(paymentService.createPayment(orderId, request));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("isAuthenticated()")
    PaymentDTO updatePaymentStatus(@PathVariable long id, @Valid @RequestBody UpdatePaymentStatusRequest request) {
        return new PaymentDTO(paymentService.updatePaymentStatus(id, request));
    }

}
