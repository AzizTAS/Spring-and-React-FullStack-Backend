package com.hoaxify.ws.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoaxify.ws.cart.CartRepository;
import com.hoaxify.ws.order.OrderRepository;
import com.hoaxify.ws.order.dto.OrderDTO;
import com.hoaxify.ws.payment.PaymentRepository;
import com.hoaxify.ws.product.ProductRepository;
import com.hoaxify.ws.user.UserRepository;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasAnyRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final PaymentRepository paymentRepository;

    public AdminController(UserRepository userRepository, ProductRepository productRepository,
            OrderRepository orderRepository, CartRepository cartRepository, PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/stats/users")
    long getTotalUsers() {
        return userRepository.count();
    }

    @GetMapping("/stats/products")
    long getTotalProducts() {
        return productRepository.count();
    }

    @GetMapping("/stats/orders")
    long getTotalOrders() {
        return orderRepository.count();
    }

    @Transactional
    @DeleteMapping("/users/{id}")
    String deleteUserById(@PathVariable Long id) {
        cartRepository.deleteByUserId(id);
        orderRepository.deleteByUserId(id);
        userRepository.deleteById(id);
        return "User deleted";
    }

    @Transactional
    @DeleteMapping("/orders/{id}")
    String deleteOrderById(@PathVariable Long id) {
        paymentRepository.deleteByOrderId(id);
        orderRepository.deleteById(id);
        return "Order deleted";
    }

    @GetMapping("/orders")
    Page<OrderDTO> getAllOrders(Pageable page) {
        return orderRepository.findAll(page).map(OrderDTO::new);
    }
}