package com.eflipkartlite.orderservice.client;

public class PaymentResponse {
    private boolean success;
    private String transactionReference;
    private String status;

    public PaymentResponse() {
    }

    public PaymentResponse(boolean success, String transactionReference, String status) {
        this.success = success;
        this.transactionReference = transactionReference;
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
