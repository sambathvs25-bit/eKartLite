package com.eflipkartlite.orderservice.service;

import com.eflipkartlite.orderservice.client.PaymentRequest;
import com.eflipkartlite.orderservice.client.PaymentResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class PaymentClient {

    private final WebClient webClient;
    
    public PaymentClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
    public PaymentResponse processPayment(PaymentRequest request, String authHeader) {
        return webClient.post()
                .uri("http://localhost:8086/customer/payments/process")
                .header("Authorization", authHeader)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PaymentResponse.class)
                .block();
    }

    public PaymentResponse paymentFallback(PaymentRequest request, String authHeader, Throwable ex) {
        return new PaymentResponse(false, "N/A", "FAILED");
    }
}
