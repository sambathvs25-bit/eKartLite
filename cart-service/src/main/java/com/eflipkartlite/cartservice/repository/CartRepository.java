package com.eflipkartlite.cartservice.repository;

import com.eflipkartlite.cartservice.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByCustomerEmail(String customerEmail);
}
