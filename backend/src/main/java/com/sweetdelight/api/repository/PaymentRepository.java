package com.sweetdelight.api.repository;

import com.sweetdelight.api.entity.Payment;
import com.sweetdelight.api.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);

    List<Payment> findByOrderIdOrderByCreatedAtDesc(Long orderId);

    Optional<Payment> findFirstByOrderIdAndStatus(Long orderId, PaymentStatus status);
}
