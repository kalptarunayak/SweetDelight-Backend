package com.sweetdelight.api.repository;

import com.sweetdelight.api.entity.Order;
import com.sweetdelight.api.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);

    @Query("SELECT o FROM Order o WHERE (:status IS NULL OR o.status = :status) ORDER BY o.createdAt DESC")
    List<Order> findByStatusOrderByCreatedAtDesc(@Param("status") OrderStatus status);
}
