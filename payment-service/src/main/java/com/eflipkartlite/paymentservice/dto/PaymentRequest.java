package com.eflipkartlite.paymentservice.dto;

import com.eflipkartlite.paymentservice.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class PaymentRequest {
    @NotNull
    private Long orderId;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private PaymentMethod method;

    public PaymentRequest() {
    }

    public PaymentRequest(Long orderId, BigDecimal amount, PaymentMethod method) {
        this.orderId = orderId;
        this.amount = amount;
        this.method = method;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }
}
