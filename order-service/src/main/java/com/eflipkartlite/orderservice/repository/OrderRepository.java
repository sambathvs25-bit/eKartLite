package com.eflipkartlite.orderservice.repository;

import com.eflipkartlite.orderservice.entity.Order;
import com.eflipkartlite.orderservice.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerEmailOrderByCreatedAtDesc(String customerEmail);
    List<Order> findByStatus(OrderStatus status);
}
