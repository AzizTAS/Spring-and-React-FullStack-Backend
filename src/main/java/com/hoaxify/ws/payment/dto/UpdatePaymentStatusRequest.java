package com.hoaxify.ws.payment.dto;

import com.hoaxify.ws.payment.PaymentStatus;

import jakarta.validation.constraints.NotNull;

public class UpdatePaymentStatusRequest {

    @NotNull(message = "{hoaxify.constraints.paymentStatus.NotNull.message}")
    private PaymentStatus status;

    private String transactionId;

    // Getters and Setters
    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

}
