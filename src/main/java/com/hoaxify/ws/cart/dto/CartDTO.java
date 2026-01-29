package com.hoaxify.ws.cart.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.hoaxify.ws.cart.Cart;

public class CartDTO {

    private long id;
    private long userId;
    private List<CartItemDTO> items;
    private BigDecimal totalAmount;

    public CartDTO() {
    }

    public CartDTO(Cart cart) {
        this.id = cart.getId();
        this.userId = cart.getUser().getId();
        this.items = cart.getItems() != null ? cart.getItems().stream().map(item -> {
            CartItemDTO dto = new CartItemDTO();
            dto.setId(item.getId());
            dto.setProductId(item.getProduct().getId());
            dto.setProductName(item.getProduct().getName());
            dto.setQuantity(item.getQuantity());
            dto.setPriceAtTime(item.getPriceAtTime());
            dto.setTotalPrice(item.getPriceAtTime().multiply(new BigDecimal(item.getQuantity())));
            return dto;
        }).collect(Collectors.toList()) : new java.util.ArrayList<>();

        this.totalAmount = this.items.stream()
                .map(CartItemDTO::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<CartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

}
