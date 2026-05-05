package com.novabank.authservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Test endpoint
    @GetMapping("/health")
    public String health() {
        return "Auth Service is running!";
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<String> login() {
        return ResponseEntity.ok("Login successful!");
    }

    // Register endpoint
    @PostMapping("/register")
    public ResponseEntity<String> register() {
        return ResponseEntity.ok("User registered!");
    }
}