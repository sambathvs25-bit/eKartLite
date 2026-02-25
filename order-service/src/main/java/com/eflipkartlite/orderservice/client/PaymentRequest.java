package com.eflipkartlite.orderservice.client;

import java.math.BigDecimal;

public class PaymentRequest {
    private Long orderId;
    private BigDecimal amount;
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
