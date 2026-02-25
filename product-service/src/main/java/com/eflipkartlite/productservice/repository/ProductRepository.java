package com.eflipkartlite.productservice.repository;

import com.eflipkartlite.productservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCaseAndCategoryContainingIgnoreCase(String name, String category);

    @Query(value = "SELECT * FROM product p WHERE p.price < :maxPrice AND p.quantity > 0", nativeQuery = true)
    List<Product> findAvailableProductsBelowPrice(@Param("maxPrice") BigDecimal maxPrice);
}
