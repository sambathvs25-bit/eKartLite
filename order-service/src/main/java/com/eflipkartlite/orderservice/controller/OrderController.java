package com.eflipkartlite.orderservice.controller;

import com.eflipkartlite.orderservice.dto.*;
import com.eflipkartlite.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    private final OrderService service;
    
    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping("/customer/orders")
    public ResponseEntity<OrderResponse> createOrder(Authentication authentication,
                                                     @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(service.createOrder(authentication.getName(), request));
    }

    @PostMapping("/customer/orders/{orderId}/pay")
    public ResponseEntity<PaymentResultResponse> payOrder(Authentication authentication,
                                                          @PathVariable("orderId") Long orderId,
                                                          @Valid @RequestBody PayOrderRequest request,
                                                          @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(service.payOrder(authentication.getName(), orderId, request, authHeader));
    }

    @GetMapping("/customer/orders")
    public ResponseEntity<List<OrderResponse>> customerOrders(Authentication authentication) {
        return ResponseEntity.ok(service.customerOrders(authentication.getName()));
    }

    @GetMapping("/agent/orders/ready-for-shipping")
    public ResponseEntity<List<OrderResponse>> readyForShipping() {
        return ResponseEntity.ok(service.readyForShipping());
    }
}
