package com.eflipkartlite.authservice.controller;

import com.eflipkartlite.authservice.dto.*;
import com.eflipkartlite.authservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final String CUSTOMER_REGISTER_SUCCESS = "Customer registered successfully";
    private static final String AGENT_REGISTER_SUCCESS = "Agent registered successfully";

    private final AuthService authService;
    
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> registerCustomer(@Valid @RequestBody RegisterCustomerRequest request) {
        authService.registerCustomer(request);
        return ResponseEntity.ok(new MessageResponse(CUSTOMER_REGISTER_SUCCESS));
    }

    @PostMapping("/register-agent")
    public ResponseEntity<MessageResponse> registerAgent(@Valid @RequestBody RegisterAgentRequest request) {
        authService.registerAgent(request);
        return ResponseEntity.ok(new MessageResponse(AGENT_REGISTER_SUCCESS));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
