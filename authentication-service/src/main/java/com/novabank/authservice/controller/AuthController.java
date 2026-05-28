package com.novabank.authservice.controller;

import com.novabank.authservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    // Health check
    @GetMapping("/health")
    public String health() {
        return "Auth Service is running!";
    }

    // Register new user
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String email = request.get("email");
            String password = request.get("password");

            if (username == null || email == null 
                    || password == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", 
                        "All fields are required!"));
            }

            Map<String, String> response = 
                userService.register(
                    username, email, password);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");

            Map<String, String> response = 
                userService.login(username, password);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(401)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Validate token
    @PostMapping("/validate")
    public ResponseEntity<?> validate(
            @RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            return ResponseEntity.ok(Map.of(
                "valid", true));
        } catch (Exception e) {
            return ResponseEntity.status(401)
                .body(Map.of("valid", false));
        }
    }
}