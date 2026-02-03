package com.hoaxify.ws.cart;

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

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    @PostMapping("/test-post")
    public ResponseEntity<String> testPost(@RequestBody AddToCartRequest request) {
        return ResponseEntity.ok("Received: productId=" + request.getProductId() + ", qty=" + request.getQuantity());
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CartDTO> getCart(@AuthenticationPrincipal CurrentUser currentUser) {
        Cart cart = cartService.getOrCreateCart(currentUser);
        return ResponseEntity.ok(CartDTO.fromEntity(cart));
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> addToCart(@AuthenticationPrincipal CurrentUser currentUser,
            @RequestBody AddToCartRequest request) {
        try {
            if (request == null || request.getProductId() == null) {
                return ResponseEntity.badRequest().body("ProductId is required");
            }
            int qty = request.getQuantity() > 0 ? request.getQuantity() : 1;
            cartService.addToCart(currentUser, request.getProductId(), qty);
            return ResponseEntity.ok("Added to cart");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/item/{cartItemId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> removeFromCart(@AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long cartItemId) {
        cartService.removeFromCart(currentUser, cartItemId);
        return ResponseEntity.ok("Removed from cart");
    }

    @PutMapping("/item/{cartItemId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> updateQuantity(@AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long cartItemId, @RequestBody UpdateQuantityRequest request) {
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
