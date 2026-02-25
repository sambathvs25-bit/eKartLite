package com.eflipkartlite.paymentservice.service;

import com.eflipkartlite.paymentservice.dto.PaymentRequest;
import com.eflipkartlite.paymentservice.dto.PaymentResponse;
import com.eflipkartlite.paymentservice.entity.Payment;
import com.eflipkartlite.paymentservice.entity.PaymentStatus;
import com.eflipkartlite.paymentservice.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository repository;
    
    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

    public PaymentResponse process(String customerEmail, PaymentRequest request) {
        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setCustomerEmail(customerEmail);
        payment.setAmount(request.getAmount());
        payment.setMethod(request.getMethod());
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionReference("TXN-" + UUID.randomUUID());
        payment.setCreatedAt(LocalDateTime.now());
        repository.save(payment);

        return new PaymentResponse(true, payment.getTransactionReference(), payment.getStatus().name());
    }
}
