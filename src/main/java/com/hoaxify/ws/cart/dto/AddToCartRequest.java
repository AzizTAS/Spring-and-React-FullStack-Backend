package com.hoaxify.ws.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AddToCartRequest {

    @NotNull(message = "{hoaxify.constraints.product.id.NotNull.message}")
    private Long productId;

    @NotNull(message = "{hoaxify.constraints.quantity.NotNull.message}")
    @Min(value = 1, message = "{hoaxify.constraints.quantity.Min.message}")
    private Integer quantity;

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}
