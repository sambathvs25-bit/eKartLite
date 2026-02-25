package com.eflipkartlite.productservice.controller;

import com.eflipkartlite.productservice.dto.*;
import com.eflipkartlite.productservice.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class ProductController {

    private final ProductService service;
    
    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping("/agent/products")
    public ResponseEntity<ProductResponse> addProduct(@Valid @RequestBody ProductCreateRequest request) {
        return ResponseEntity.ok(service.addProduct(request));
    }

    @PutMapping("/agent/products/{productId}/price")
    public ResponseEntity<MessageResponse> updatePrice(@PathVariable("productId") Long productId,
                                                       @Valid @RequestBody PriceUpdateRequest request) {
        service.updatePrice(productId, request.getPrice());
        return ResponseEntity.ok(new MessageResponse("Price updated"));
    }

    @PutMapping("/agent/products/{productId}/quantity")
    public ResponseEntity<MessageResponse> updateQuantity(@PathVariable("productId") Long productId,
                                                          @Valid @RequestBody QuantityUpdateRequest request) {
        service.updateQuantity(productId, request.getQuantity());
        return ResponseEntity.ok(new MessageResponse("Quantity updated"));
    }

    @GetMapping("/customer/products/search")
    public ResponseEntity<List<ProductResponse>> search(@RequestParam(value = "name", defaultValue = "") String name,
                                                        @RequestParam(value = "category", defaultValue = "") String category) {
        return ResponseEntity.ok(service.search(name, category));
    }

    @GetMapping("/customer/products/native-search")
    public ResponseEntity<List<ProductResponse>> nativeSearch(@RequestParam("maxPrice") BigDecimal maxPrice) {
        return ResponseEntity.ok(service.nativeSearchBelowPrice(maxPrice));
    }
}
