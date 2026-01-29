package com.hoaxify.ws.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hoaxify.ws.cart.Cart;
import com.hoaxify.ws.cart.CartItem;
import com.hoaxify.ws.cart.CartService;
import com.hoaxify.ws.configuration.CurrentUser;
import com.hoaxify.ws.order.dto.CreateOrderRequest;
import com.hoaxify.ws.order.dto.UpdateOrderStatusRequest;
import com.hoaxify.ws.order.exception.OrderNotFoundException;
import com.hoaxify.ws.product.ProductService;
import com.hoaxify.ws.user.User;
import com.hoaxify.ws.user.UserService;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final UserService userService;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
            CartService cartService, UserService userService, ProductService productService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartService = cartService;
        this.userService = userService;
        this.productService = productService;
    }

    public Page<Order> getUserOrders(CurrentUser currentUser, Pageable page) {
        return orderRepository.findByUserId(currentUser.getId(), page);
    }

    public Order getOrder(long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    public Order createOrder(CurrentUser currentUser, CreateOrderRequest request) {
        User user = userService.getUser(currentUser.getId());
        Cart cart = cartService.getOrCreateCart(currentUser);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        for (CartItem cartItem : cart.getItems()) {
            productService.decreaseStock(cartItem.getProduct().getId(), cartItem.getQuantity());
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(request.getShippingAddress());

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPriceAtTime());
            orderItem.setProductName(cartItem.getProduct().getName());

            BigDecimal itemTotal = cartItem.getPriceAtTime()
                    .multiply(new BigDecimal(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            order.getItems().add(orderItem);
        }

        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(currentUser);

        return savedOrder;
    }

    public Order updateOrderStatus(long id, UpdateOrderStatusRequest request) {
        Order order = getOrder(id);
        order.setStatus(request.getStatus());
        order.setUpdatedDate(LocalDateTime.now());
        return orderRepository.save(order);
    }

    public void deleteOrder(long id) {
        Order order = getOrder(id);
        orderItemRepository.deleteAll(order.getItems());
        orderRepository.delete(order);
    }
}