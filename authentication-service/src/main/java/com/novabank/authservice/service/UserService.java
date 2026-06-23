package com.novabank.authservice.service;

import com.novabank.authservice.model.User;
import com.novabank.authservice.repository.UserRepository;
import com.novabank.authservice.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.HashMap;
import jakarta.annotation.PostConstruct;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder passwordEncoder = 
        new BCryptPasswordEncoder();

    // Register new user
    public Map<String, String> register(
            String username, String email, String password) {
        
        // Check if username already exists
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException(
                "Username already exists!");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException(
                "Email already registered!");
        }

        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(
            passwordEncoder.encode(password));
        user.setRole("USER");

        userRepository.save(user);

        // Generate token
        String token = jwtUtil.generateToken(username);

        Map<String, String> response = new HashMap<>();
        response.put("message", 
            "Registration successful!");
        response.put("username", username);
        response.put("token", token);
        return response;
    }

    // Login user
    public Map<String, String> login(
            String username, String password) {

        // Find user by username
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> 
                new RuntimeException(
                    "Invalid username or password!"));

        // Check password
        if (!passwordEncoder.matches(
                password, user.getPassword())) {
            throw new RuntimeException(
                "Invalid username or password!");
        }

        // Generate token
        String token = jwtUtil.generateToken(username);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Login successful!");
        response.put("username", username);
        response.put("token", token);
        return response;
    }

    @PostConstruct
  public void createDefaultUser() {
    if (!userRepository.existsByUsername("vinod")) {
        User user = new User();
        user.setUsername("vinod");
        user.setEmail("vinod@novabank.com");
        user.setPassword(
            passwordEncoder.encode("vinod123"));
        user.setRole("ADMIN");
        userRepository.save(user);
        System.out.println(
            "✅ Default user created: vinod/vinod123");
    }
}
}