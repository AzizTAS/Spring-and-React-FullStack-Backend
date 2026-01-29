package com.hoaxify.ws.cart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.hoaxify.ws.configuration.CurrentUser;
import com.hoaxify.ws.cart.dto.AddToCartRequest;
import com.hoaxify.ws.cart.dto.CartDTO;
import com.hoaxify.ws.cart.dto.UpdateQuantityRequest;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private static final Logger log = LoggerFactory.getLogger(CartController.class);
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CartDTO> getCart(@AuthenticationPrincipal CurrentUser currentUser) {
        Cart cart = cartService.getOrCreateCart(currentUser);
        return ResponseEntity.ok(CartDTO.fromEntity(cart));
    }

    @GetMapping("/test-add/{productId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> testAddToCart(@AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long productId) {
        try {
            log.info("=== TEST ADD TO CART ===");
            log.info("CurrentUser ID: {}", currentUser != null ? currentUser.getId() : "null");
            log.info("ProductId: {}", productId);
            
            cartService.addToCart(currentUser, productId, 1);
            return ResponseEntity.ok("Added to cart via GET test");
        } catch (Exception e) {
            log.error("Error in test add to cart", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getClass().getName() + " - " + e.getMessage());
        }
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> addToCart(@AuthenticationPrincipal CurrentUser currentUser,
            @RequestBody(required = false) AddToCartRequest request) {
        try {
            log.info("=== ADD TO CART REQUEST ===");
            log.info("CurrentUser: {}", currentUser != null ? currentUser.getId() : "null");
            log.info("Request: {}", request != null ? "productId=" + request.getProductId() + ", quantity=" + request.getQuantity() : "null");

            if (request == null) {
                return ResponseEntity.badRequest().body("Request body is null");
            }
            if (request.getProductId() == null) {
                return ResponseEntity.badRequest().body("ProductId is null");
            }

            cartService.addToCart(currentUser, request.getProductId(), request.getQuantity());
            return ResponseEntity.ok("Added to cart");
        } catch (Exception e) {
            log.error("Error adding to cart", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getClass().getName() + " - " + e.getMessage());
        }
    }

    @DeleteMapping("/item/{cartItemId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> removeFromCart(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long cartItemId) {
        cartService.removeFromCart(currentUser, cartItemId);
        return ResponseEntity.ok("Removed from cart");
    }

    @PutMapping("/item/{cartItemId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> updateQuantity(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long cartItemId,
            @RequestBody UpdateQuantityRequest request) {
        cartService.updateCartItemQuantity(currentUser, cartItemId, request.getQuantity());
        return ResponseEntity.ok("Quantity updated");
    }

    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> clearCart(@AuthenticationPrincipal CurrentUser currentUser) {
        cartService.clearCart(currentUser);
        return ResponseEntity.ok("Cart cleared");
    }
}