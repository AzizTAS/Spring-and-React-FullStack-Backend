package com.hoaxify.ws.cart;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hoaxify.ws.configuration.CurrentUser;
import com.hoaxify.ws.product.Product;
import com.hoaxify.ws.product.ProductService;
import com.hoaxify.ws.user.User;
import com.hoaxify.ws.user.UserService;

@Service
@Transactional
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

    public void addToCart(CurrentUser currentUser, Long productId, int quantity) {
        Cart cart = getOrCreateCart(currentUser);
        Product product = productService.getProduct(productId);

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setPriceAtTime(product.getPrice());
        }
        cartItemRepository.save(cartItem);
    }

    public void removeFromCart(CurrentUser currentUser, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));

        if (cartItem.getCart().getUser().getId() != currentUser.getId()) {
            throw new RuntimeException("Unauthorized");
        }
        cartItemRepository.delete(cartItem);
    }

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

    public void clearCart(CurrentUser currentUser) {
        Cart cart = getOrCreateCart(currentUser);
        cartItemRepository.deleteByCartId(cart.getId());
    }
}
