package com.eflipkartlite.productservice.service;

import com.eflipkartlite.productservice.dto.*;
import com.eflipkartlite.productservice.entity.Product;
import com.eflipkartlite.productservice.exception.BusinessException;
import com.eflipkartlite.productservice.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {
    private static final String PRODUCT_SEARCH_CACHE = "product-search-v2";

    private final ProductRepository repository;
    
    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @CacheEvict(value = PRODUCT_SEARCH_CACHE, allEntries = true)
    public ProductResponse addProduct(ProductCreateRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setCategory(request.getCategory());
        product.setStatus(request.getStatus());
        return toResponse(repository.save(product));
    }

    @CacheEvict(value = PRODUCT_SEARCH_CACHE, allEntries = true)
    public void updatePrice(Long productId, BigDecimal price) {
        Product product = repository.findById(productId).orElseThrow(() -> new BusinessException("Product not found"));
        product.setPrice(price);
        repository.save(product);
    }

    @CacheEvict(value = PRODUCT_SEARCH_CACHE, allEntries = true)
    public void updateQuantity(Long productId, Integer quantity) {
        Product product = repository.findById(productId).orElseThrow(() -> new BusinessException("Product not found"));
        product.setQuantity(quantity);
        repository.save(product);
    }

    @Cacheable(value = PRODUCT_SEARCH_CACHE, key = "#name + '-' + #category")
    public List<ProductResponse> search(String name, String category) {
        return repository.findByNameContainingIgnoreCaseAndCategoryContainingIgnoreCase(name, category)
                .stream().map(this::toResponse).toList();
    }

    public List<ProductResponse> nativeSearchBelowPrice(BigDecimal maxPrice) {
        return repository.findAvailableProductsBelowPrice(maxPrice).stream().map(this::toResponse).toList();
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getQuantity(), p.getCategory(), p.getStatus());
    }
}
