package com.novabank.authservice.service;

import com.novabank.authservice.model.User;
import com.novabank.authservice.repository.UserRepository;
import com.novabank.authservice.security.JwtUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder passwordEncoder =
        new BCryptPasswordEncoder();

    // Create default user on startup
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

    // Register new user
    public Map<String, String> register(
            String username, String email, 
            String password) {

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException(
                "Username already exists!");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException(
                "Email already registered!");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(
            passwordEncoder.encode(password));
        user.setRole("USER");

        userRepository.save(user);

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

        User user = userRepository
            .findByUsername(username)
            .orElseThrow(() ->
                new RuntimeException(
                    "Invalid username or password!"));

        if (!passwordEncoder.matches(
                password, user.getPassword())) {
            throw new RuntimeException(
                "Invalid username or password!");
        }

        String token = jwtUtil.generateToken(username);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Login successful!");
        response.put("username", username);
        response.put("token", token);
        return response;
    }

    // Change password
    public Map<String, String> changePassword(
            String username,
            String currentPassword,
            String newPassword) {

        User user = userRepository
            .findByUsername(username)
            .orElseThrow(() ->
                new RuntimeException("User not found!"));

        if (!passwordEncoder.matches(
                currentPassword, user.getPassword())) {
            throw new RuntimeException(
                "Current password is incorrect!");
        }

        if (newPassword.length() < 6) {
            throw new RuntimeException(
                "New password must be at least 6 characters!");
        }

        user.setPassword(
            passwordEncoder.encode(newPassword));
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message",
            "Password changed successfully!");
        return response;
    }
}