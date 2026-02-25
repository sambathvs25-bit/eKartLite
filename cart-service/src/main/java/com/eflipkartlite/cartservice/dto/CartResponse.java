package com.eflipkartlite.cartservice.dto;

import java.math.BigDecimal;
import java.util.List;

public class CartResponse {
    private Long cartId;
    private String customerEmail;
    private List<CartItemResponse> items;
    private BigDecimal grandTotal;

    public CartResponse() {
    }

    public CartResponse(Long cartId, String customerEmail, List<CartItemResponse> items, BigDecimal grandTotal) {
        this.cartId = cartId;
        this.customerEmail = customerEmail;
        this.items = items;
        this.grandTotal = grandTotal;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public void setItems(List<CartItemResponse> items) {
        this.items = items;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }
}
