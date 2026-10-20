package com.sweetdelight.api.repository;

import com.sweetdelight.api.entity.OrderStatusEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderStatusEventRepository extends JpaRepository<OrderStatusEvent, Long> {
    List<OrderStatusEvent> findByOrderIdOrderByCreatedAtAsc(Long orderId);
}
