package com.eflipkartlite.authservice.config;

import com.eflipkartlite.authservice.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BootstrapConfig {

    private final AuthService authService;

    public BootstrapConfig(AuthService authService) {
        this.authService = authService;
    }

    @Bean
    CommandLineRunner initAgent() {
        return args -> authService.seedDefaultAgent();
    }
}
