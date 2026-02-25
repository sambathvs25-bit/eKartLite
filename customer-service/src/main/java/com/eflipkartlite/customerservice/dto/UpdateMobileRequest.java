package com.eflipkartlite.customerservice.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateMobileRequest {
    @NotBlank
    private String mobileNumber;

    public UpdateMobileRequest() {
    }

    public UpdateMobileRequest(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
