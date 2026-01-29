package com.hoaxify.ws.cart.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

import com.hoaxify.ws.cart.Cart;

public class CartDTO {

    private long id;
    private long userId;
    private List<CartItemDTO> items;
    private BigDecimal totalAmount;

    public CartDTO() {
    }

    public static CartDTO fromEntity(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUser().getId());
        
        if (cart.getItems() != null && !cart.getItems().isEmpty()) {
            dto.setItems(cart.getItems().stream().map(item -> {
                CartItemDTO itemDTO = new CartItemDTO();
                itemDTO.setId(item.getId());
                itemDTO.setProductId(item.getProduct().getId());
                itemDTO.setProductName(item.getProduct().getName());
                itemDTO.setQuantity(item.getQuantity());
                itemDTO.setPriceAtTime(item.getPriceAtTime());
                itemDTO.setTotalPrice(item.getPriceAtTime().multiply(new BigDecimal(item.getQuantity())));
                return itemDTO;
            }).collect(Collectors.toList()));
        } else {
            dto.setItems(new ArrayList<>());
        }
        
        dto.setTotalAmount(dto.getItems().stream()
                .map(CartItemDTO::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        
        return dto;
    }

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
