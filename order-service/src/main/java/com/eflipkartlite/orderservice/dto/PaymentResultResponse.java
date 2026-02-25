package com.eflipkartlite.orderservice.dto;

import com.eflipkartlite.orderservice.entity.OrderStatus;

public class PaymentResultResponse {
    private Long orderId;
    private boolean paymentSuccess;
    private String transactionReference;
    private OrderStatus orderStatus;
    private String message;

    public PaymentResultResponse() {
    }

    public PaymentResultResponse(Long orderId, boolean paymentSuccess, String transactionReference, OrderStatus orderStatus, String message) {
        this.orderId = orderId;
        this.paymentSuccess = paymentSuccess;
        this.transactionReference = transactionReference;
        this.orderStatus = orderStatus;
        this.message = message;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public boolean isPaymentSuccess() {
        return paymentSuccess;
    }

    public void setPaymentSuccess(boolean paymentSuccess) {
        this.paymentSuccess = paymentSuccess;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
