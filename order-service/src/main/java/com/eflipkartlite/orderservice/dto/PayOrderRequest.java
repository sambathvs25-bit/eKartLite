package com.eflipkartlite.orderservice.dto;

import com.eflipkartlite.orderservice.client.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public class PayOrderRequest {
    @NotNull
    private PaymentMethod method;

    public PayOrderRequest() {
    }

    public PayOrderRequest(PaymentMethod method) {
        this.method = method;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }
}
