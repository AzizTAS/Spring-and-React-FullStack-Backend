package com.hoaxify.ws.cart;

import org.springframework.stereotype.Service;

import com.hoaxify.ws.cart.exception.CartNotFoundException;
import com.hoaxify.ws.configuration.CurrentUser;
import com.hoaxify.ws.product.Product;
import com.hoaxify.ws.product.ProductService;
import com.hoaxify.ws.user.User;
import com.hoaxify.ws.user.UserService;

import jakarta.transaction.Transactional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    private final ProductService productService;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
            UserService userService, ProductService productService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userService = userService;
        this.productService = productService;
    }

    @Transactional
    public Cart getOrCreateCart(CurrentUser currentUser) {
        Cart cart = cartRepository.findByUserId(currentUser.getId());
        if (cart == null) {
            cart = new Cart();
            User user = userService.getUser(currentUser.getId());
            cart.setUser(user);
            cart = cartRepository.save(cart);
        }
        return cart;
    }

    public Cart getCart(long id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new CartNotFoundException(id));
    }

    @Transactional
    public void addToCart(CurrentUser currentUser, long productId, int quantity) {
        Cart cart = getOrCreateCart(currentUser);
        Product product = productService.getProduct(productId);

        // Check if product already in cart
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepository.save(cartItem);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setPriceAtTime(product.getPrice());
            cartItemRepository.save(cartItem);
        }
    }

    @Transactional
    public void removeFromCart(CurrentUser currentUser, long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));

        // Verify user owns this cart
        if (cartItem.getCart().getUser().getId() != currentUser.getId()) {
            throw new RuntimeException("Unauthorized");
        }

        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public void updateCartItemQuantity(CurrentUser currentUser, long cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));

        // Verify user owns this cart
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
