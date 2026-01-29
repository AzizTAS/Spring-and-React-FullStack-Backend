package com.hoaxify.ws.order.dto;

import com.hoaxify.ws.order.OrderStatus;

import jakarta.validation.constraints.NotNull;

public class UpdateOrderStatusRequest {

    @NotNull(message = "{hoaxify.constraints.status.NotNull.message}")
    private OrderStatus status;

    // Getters and Setters
    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

}
