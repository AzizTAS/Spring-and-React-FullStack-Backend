package com.hoaxify.ws.cart.exception;

public class CartNotFoundException extends RuntimeException {

    public CartNotFoundException(long id) {
        super("Cart not found with id: " + id);
    }

    public CartNotFoundException(String message) {
        super(message);
    }

}
