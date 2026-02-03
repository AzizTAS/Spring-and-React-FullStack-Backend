package com.hoaxify.ws.payment;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hoaxify.ws.order.Order;
import com.hoaxify.ws.order.OrderService;
import com.hoaxify.ws.order.OrderStatus;
import com.hoaxify.ws.payment.dto.CreatePaymentRequest;
import com.hoaxify.ws.payment.dto.UpdatePaymentStatusRequest;
import com.hoaxify.ws.payment.exception.PaymentNotFoundException;

import jakarta.transaction.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    public PaymentService(PaymentRepository paymentRepository, OrderService orderService) {
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
    }

    public Payment getPayment(long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
    }

    public Payment getPaymentByOrderId(long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId);
        if (payment == null) {
            throw new PaymentNotFoundException("Payment not found for order: " + orderId);
        }
        return payment;
    }

    @Transactional
    public Payment createPayment(long orderId, CreatePaymentRequest request) {
        Order order = orderService.getOrder(orderId);

        Payment existingPayment = paymentRepository.findByOrderId(orderId);
        if (existingPayment != null && existingPayment.getStatus() == PaymentStatus.COMPLETED) {
            throw new RuntimeException("Payment already completed for this order");
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setDescription(request.getDescription());
        payment.setTransactionId(UUID.randomUUID().toString());
        
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setCompletedDate(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);
        
        com.hoaxify.ws.order.dto.UpdateOrderStatusRequest statusRequest = 
            new com.hoaxify.ws.order.dto.UpdateOrderStatusRequest();
        statusRequest.setStatus(OrderStatus.CONFIRMED);
        orderService.updateOrderStatus(orderId, statusRequest);

        return savedPayment;
    }

    @Transactional
    public Payment updatePaymentStatus(long id, UpdatePaymentStatusRequest request) {
        Payment payment = getPayment(id);
        payment.setStatus(request.getStatus());

        if (request.getTransactionId() != null) {
            payment.setTransactionId(request.getTransactionId());
        }

        if (request.getStatus() == PaymentStatus.COMPLETED) {
            payment.setCompletedDate(LocalDateTime.now());
            com.hoaxify.ws.order.dto.UpdateOrderStatusRequest statusRequest = 
                new com.hoaxify.ws.order.dto.UpdateOrderStatusRequest();
            statusRequest.setStatus(OrderStatus.CONFIRMED);
            orderService.updateOrderStatus(payment.getOrder().getId(), statusRequest);
        }

        return paymentRepository.save(payment);
    }

}
