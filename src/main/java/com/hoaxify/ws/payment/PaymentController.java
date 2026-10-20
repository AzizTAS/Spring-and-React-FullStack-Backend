package com.hoaxify.ws.payment;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoaxify.ws.configuration.CurrentUser;
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
    PaymentDTO getPayment(@AuthenticationPrincipal CurrentUser currentUser, @PathVariable long id) {
        return new PaymentDTO(checkAccess(currentUser, paymentService.getPayment(id)));
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("isAuthenticated()")
    PaymentDTO getPaymentByOrderId(@AuthenticationPrincipal CurrentUser currentUser, @PathVariable long orderId) {
        return new PaymentDTO(checkAccess(currentUser, paymentService.getPaymentByOrderId(orderId)));
    }

    private Payment checkAccess(CurrentUser currentUser, Payment payment) {
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && payment.getOrder().getUser().getId() != currentUser.getId()) {
            throw new AccessDeniedException("Unauthorized");
        }
        return payment;
    }

    @PostMapping("/order/{orderId}")
    @PreAuthorize("isAuthenticated()")
    PaymentDTO createPayment(@AuthenticationPrincipal CurrentUser currentUser, @PathVariable long orderId,
            @Valid @RequestBody CreatePaymentRequest request) {
        return new PaymentDTO(paymentService.createPayment(currentUser, orderId, request));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    PaymentDTO updatePaymentStatus(@PathVariable long id, @Valid @RequestBody UpdatePaymentStatusRequest request) {
        return new PaymentDTO(paymentService.updatePaymentStatus(id, request));
    }

}
