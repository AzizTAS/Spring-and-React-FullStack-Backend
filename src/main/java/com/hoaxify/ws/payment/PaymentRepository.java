package com.hoaxify.ws.payment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByOrderId(long orderId);
    Payment findByTransactionId(String transactionId);
    void deleteByOrderId(Long orderId);
}