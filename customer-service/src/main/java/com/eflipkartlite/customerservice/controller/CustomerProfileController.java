package com.eflipkartlite.customerservice.controller;

import com.eflipkartlite.customerservice.dto.CustomerProfileResponse;
import com.eflipkartlite.customerservice.dto.MessageResponse;
import com.eflipkartlite.customerservice.dto.UpdateMobileRequest;
import com.eflipkartlite.customerservice.service.CustomerProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer/profile")
public class CustomerProfileController {

    private final CustomerProfileService service;
    
    public CustomerProfileController(CustomerProfileService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public ResponseEntity<CustomerProfileResponse> me(Authentication authentication) {
        return ResponseEntity.ok(service.myProfile(authentication.getName()));
    }

    @PutMapping("/mobile")
    public ResponseEntity<MessageResponse> updateMobile(Authentication authentication, @Valid @RequestBody UpdateMobileRequest request) {
        service.updateMobile(authentication.getName(), request.getMobileNumber());
        return ResponseEntity.ok(new MessageResponse("Mobile number updated"));
    }
}
