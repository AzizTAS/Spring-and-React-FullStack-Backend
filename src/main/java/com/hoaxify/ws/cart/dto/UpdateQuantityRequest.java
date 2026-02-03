package com.hoaxify.ws.cart.dto;

public class UpdateQuantityRequest {
    private int quantity;

    public UpdateQuantityRequest() {}

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
