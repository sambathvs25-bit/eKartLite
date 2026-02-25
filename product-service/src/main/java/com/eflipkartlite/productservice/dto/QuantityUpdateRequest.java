package com.eflipkartlite.productservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class QuantityUpdateRequest {
    @NotNull
    @PositiveOrZero
    private Integer quantity;

    public QuantityUpdateRequest() {
    }

    public QuantityUpdateRequest(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
