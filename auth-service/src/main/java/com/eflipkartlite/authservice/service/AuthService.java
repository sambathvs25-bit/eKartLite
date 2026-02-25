package com.eflipkartlite.authservice.service;

import com.eflipkartlite.authservice.dto.AuthResponse;
import com.eflipkartlite.authservice.dto.LoginRequest;
import com.eflipkartlite.authservice.dto.RegisterAgentRequest;
import com.eflipkartlite.authservice.dto.RegisterCustomerRequest;
import com.eflipkartlite.authservice.entity.Agent;
import com.eflipkartlite.authservice.entity.Customer;
import com.eflipkartlite.authservice.entity.Role;
import com.eflipkartlite.authservice.exception.BusinessException;
import com.eflipkartlite.authservice.repository.AgentRepository;
import com.eflipkartlite.authservice.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final String DEFAULT_AGENT_NAME = "Default Agent";
    private static final String DEFAULT_AGENT_EMAIL = "agent@eflipkartlite.com";
    private static final String DEFAULT_AGENT_PASSWORD = "agent123";
    private static final String EMAIL_ALREADY_REGISTERED = "Email is already registered";
    private static final String INVALID_CREDENTIALS = "Invalid credentials";

    private final CustomerRepository customerRepository;
    private final AgentRepository agentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
    public AuthService(CustomerRepository customerRepository, AgentRepository agentRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.customerRepository = customerRepository;
        this.agentRepository = agentRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void registerCustomer(RegisterCustomerRequest request) {
        ensureEmailIsNotAlreadyUsed(request.getEmail());
        customerRepository.save(toCustomerEntity(request));
    }

    public void registerAgent(RegisterAgentRequest request) {
        ensureEmailIsNotAlreadyUsed(request.getEmail());
        agentRepository.save(toAgentEntity(request));
    }

    public AuthResponse login(LoginRequest request) {
        var customerOptional = customerRepository.findByEmail(request.getEmail());
        if (customerOptional.isPresent() && passwordMatches(request.getPassword(), customerOptional.get().getPassword())) {
            Customer customer = customerOptional.get();
            return buildAuthResponse(customer.getEmail(), customer.getRole());
        }

        var agentOptional = agentRepository.findByEmail(request.getEmail());
        if (agentOptional.isPresent() && passwordMatches(request.getPassword(), agentOptional.get().getPassword())) {
            Agent agent = agentOptional.get();
            return buildAuthResponse(agent.getEmail(), agent.getRole());
        }

        throw new BusinessException(INVALID_CREDENTIALS);
    }

    public void seedDefaultAgent() {
        if (!agentRepository.existsByEmail(DEFAULT_AGENT_EMAIL)) {
            Agent agent = new Agent();
            agent.setName(DEFAULT_AGENT_NAME);
            agent.setEmail(DEFAULT_AGENT_EMAIL);
            agent.setPassword(passwordEncoder.encode(DEFAULT_AGENT_PASSWORD));
            agent.setRole(Role.AGENT);
            agentRepository.save(agent);
        }
    }

    private void ensureEmailIsNotAlreadyUsed(String email) {
        if (customerRepository.existsByEmail(email) || agentRepository.existsByEmail(email)) {
            throw new BusinessException(EMAIL_ALREADY_REGISTERED);
        }
    }

    private Customer toCustomerEntity(RegisterCustomerRequest request) {
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setMobileNumber(request.getMobileNumber());
        customer.setRole(Role.CUSTOMER);
        return customer;
    }

    private Agent toAgentEntity(RegisterAgentRequest request) {
        Agent agent = new Agent();
        agent.setName(request.getName());
        agent.setEmail(request.getEmail());
        agent.setPassword(passwordEncoder.encode(request.getPassword()));
        agent.setRole(Role.AGENT);
        return agent;
    }

    private boolean passwordMatches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    private AuthResponse buildAuthResponse(String email, Role role) {
        String token = jwtService.generateToken(email, role);
        return new AuthResponse(token, role.name(), email);
    }
}


