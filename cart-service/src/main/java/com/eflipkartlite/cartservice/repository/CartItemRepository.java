package com.eflipkartlite.cartservice.repository;

import com.eflipkartlite.cartservice.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
