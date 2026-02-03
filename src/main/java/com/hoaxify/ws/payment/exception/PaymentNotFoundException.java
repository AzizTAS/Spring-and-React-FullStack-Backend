package com.hoaxify.ws.payment.exception;

public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(long id) {
        super("Payment not found with id: " + id);
    }

    public PaymentNotFoundException(String message) {
        super(message);
    }

}
