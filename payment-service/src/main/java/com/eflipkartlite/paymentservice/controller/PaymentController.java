package com.eflipkartlite.paymentservice.controller;

import com.eflipkartlite.paymentservice.dto.PaymentRequest;
import com.eflipkartlite.paymentservice.dto.PaymentResponse;
import com.eflipkartlite.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer/payments")
public class PaymentController {

    private final PaymentService service;
    
    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> process(Authentication authentication, @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(service.process(authentication.getName(), request));
    }
}
