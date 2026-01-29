package com.hoaxify.ws.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoaxify.ws.order.Order;
import com.hoaxify.ws.order.OrderRepository;
import com.hoaxify.ws.order.dto.OrderDTO;
import com.hoaxify.ws.product.ProductRepository;
import com.hoaxify.ws.user.UserRepository;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasAnyRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public AdminController(UserRepository userRepository, ProductRepository productRepository,
            OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
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

    @GetMapping("/orders")
    Page<OrderDTO> getAllOrders(Pageable page) {
        return orderRepository.findAll(page).map(OrderDTO::new);
    }

}
