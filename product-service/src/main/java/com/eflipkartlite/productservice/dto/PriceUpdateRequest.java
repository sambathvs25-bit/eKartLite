package com.eflipkartlite.productservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class PriceUpdateRequest {
    @NotNull
    @Positive
    private BigDecimal price;

    public PriceUpdateRequest() {
    }

    public PriceUpdateRequest(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
