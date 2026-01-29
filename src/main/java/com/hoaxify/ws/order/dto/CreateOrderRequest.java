package com.hoaxify.ws.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateOrderRequest {

    @NotBlank(message = "{hoaxify.constraints.shippingAddress.NotBlank.message}")
    @Size(min = 5, max = 500, message = "{hoaxify.constraints.shippingAddress.Size.message}")
    private String shippingAddress;

    // Getters and Setters
    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

}
