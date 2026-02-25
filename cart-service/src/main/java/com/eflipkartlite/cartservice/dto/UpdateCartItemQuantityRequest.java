package com.eflipkartlite.cartservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class UpdateCartItemQuantityRequest {
    @NotNull
    @Positive
    private Integer quantity;

    public UpdateCartItemQuantityRequest() {
    }

    public UpdateCartItemQuantityRequest(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
