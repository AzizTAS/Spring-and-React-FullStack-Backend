package com.hoaxify.ws.cart;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoaxify.ws.cart.dto.AddToCartRequest;
import com.hoaxify.ws.cart.dto.CartDTO;
import com.hoaxify.ws.configuration.CurrentUser;
import com.hoaxify.ws.shared.GenericMessage;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    CartDTO getCart(@AuthenticationPrincipal CurrentUser currentUser) {
        return new CartDTO(cartService.getOrCreateCart(currentUser));
    }

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    GenericMessage addToCart(@AuthenticationPrincipal CurrentUser currentUser,
            @Valid @RequestBody AddToCartRequest request) {
        cartService.addToCart(currentUser, request.getProductId(), request.getQuantity());
        return new GenericMessage("Product added to cart successfully");
    }

    @PutMapping("/items/{cartItemId}")
    @PreAuthorize("isAuthenticated()")
    GenericMessage updateCartItem(@AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable long cartItemId,
            @RequestBody AddToCartRequest request) {
        cartService.updateCartItemQuantity(currentUser, cartItemId, request.getQuantity());
        return new GenericMessage("Cart item updated successfully");
    }

    @DeleteMapping("/items/{cartItemId}")
    @PreAuthorize("isAuthenticated()")
    GenericMessage removeFromCart(@AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable long cartItemId) {
        cartService.removeFromCart(currentUser, cartItemId);
        return new GenericMessage("Item removed from cart successfully");
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    GenericMessage clearCart(@AuthenticationPrincipal CurrentUser currentUser) {
        cartService.clearCart(currentUser);
        return new GenericMessage("Cart cleared successfully");
    }

}
