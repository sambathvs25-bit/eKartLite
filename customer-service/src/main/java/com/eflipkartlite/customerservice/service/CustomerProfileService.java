package com.eflipkartlite.customerservice.service;

import com.eflipkartlite.customerservice.dto.CustomerProfileResponse;
import com.eflipkartlite.customerservice.exception.BusinessException;
import com.eflipkartlite.customerservice.repository.CustomerProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerProfileService {

    private final CustomerProfileRepository repository;
    
    public CustomerProfileService(CustomerProfileRepository repository) {
        this.repository = repository;
    }

    public CustomerProfileResponse myProfile(String email) {
        var customer = repository.findByEmail(email).orElseThrow(() -> new BusinessException("Customer not found"));
        return new CustomerProfileResponse(customer.getId(), customer.getName(), customer.getEmail(), customer.getMobileNumber());
    }

    public void updateMobile(String email, String mobile) {
        var customer = repository.findByEmail(email).orElseThrow(() -> new BusinessException("Customer not found"));
        customer.setMobileNumber(mobile);
        repository.save(customer);
    }
}
