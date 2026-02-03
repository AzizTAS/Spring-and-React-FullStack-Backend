package com.hoaxify.ws.payment.dto;

import com.hoaxify.ws.payment.PaymentMethod;

import jakarta.validation.constraints.NotNull;

public class CreatePaymentRequest {

    @NotNull(message = "{hoaxify.constraints.paymentMethod.NotNull.message}")
    private PaymentMethod paymentMethod;

    private String description;

    // Getters and Setters
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
