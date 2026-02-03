package com.hoaxify.ws.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import com.hoaxify.ws.configuration.CurrentUser;
import com.hoaxify.ws.order.dto.CreateOrderRequest;
import com.hoaxify.ws.order.dto.OrderDTO;
import com.hoaxify.ws.order.dto.UpdateOrderStatusRequest;
import com.hoaxify.ws.shared.GenericMessage;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    Page<OrderDTO> getUserOrders(@AuthenticationPrincipal CurrentUser currentUser, Pageable page) {
        return orderService.getUserOrders(currentUser, page).map(OrderDTO::new);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    OrderDTO getOrder(@AuthenticationPrincipal CurrentUser currentUser, @PathVariable long id) {
        Order order = orderService.getOrder(id);
        if (order.getUser().getId() != currentUser.getId()) {
            throw new RuntimeException("Unauthorized");
        }
        return new OrderDTO(order);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    OrderDTO createOrder(@AuthenticationPrincipal CurrentUser currentUser,
            @Valid @RequestBody CreateOrderRequest request) {
        return new OrderDTO(orderService.createOrder(currentUser, request));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("isAuthenticated()")
    OrderDTO updateOrderStatus(@PathVariable long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return new OrderDTO(orderService.updateOrderStatus(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    GenericMessage deleteOrder(@PathVariable long id) {
        orderService.deleteOrder(id);
        return new GenericMessage("Order deleted successfully");
    }

}
