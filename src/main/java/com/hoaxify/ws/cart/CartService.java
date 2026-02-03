package com.hoaxify.ws.cart;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hoaxify.ws.configuration.CurrentUser;
import com.hoaxify.ws.product.Product;
import com.hoaxify.ws.product.ProductRepository;
import com.hoaxify.ws.user.User;
import com.hoaxify.ws.user.UserRepository;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
            UserRepository userRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Cart getOrCreateCart(CurrentUser currentUser) {
        Cart cart = cartRepository.findByUserId(currentUser.getId());
        if (cart == null) {
            cart = new Cart();
            User user = userRepository.findById(currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            cart.setUser(user);
            cart = cartRepository.save(cart);
        }
        return cart;
    }

    @Transactional
    public void addToCart(CurrentUser currentUser, Long productId, int quantity) {
        Cart cart = getOrCreateCart(currentUser);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        
        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepository.save(cartItem);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            BigDecimal price = product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO;
            cartItem.setPriceAtTime(price);
            cartItemRepository.save(cartItem);
        }
    }

    @Transactional
    public void removeFromCart(CurrentUser currentUser, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));

        if (cartItem.getCart().getUser().getId() != currentUser.getId()) {
            throw new RuntimeException("Unauthorized");
        }
        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public void updateCartItemQuantity(CurrentUser currentUser, Long cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));

        if (cartItem.getCart().getUser().getId() != currentUser.getId()) {
            throw new RuntimeException("Unauthorized");
        }

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }
    }

    @Transactional
    public void clearCart(CurrentUser currentUser) {
        Cart cart = getOrCreateCart(currentUser);
        cartItemRepository.deleteByCartId(cart.getId());
    }
}
