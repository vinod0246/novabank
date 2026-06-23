package com.novabank.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novabank.authservice.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment =
    SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test 1 - Health Check")
    void health_returnsOk() throws Exception {
        mockMvc.perform(get("/api/auth/health"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                    "Auth Service is running!"));
    }

    @Test
    @DisplayName("Test 2 - Register Success")
    void register_returnsSuccess() throws Exception {
        when(userService.register(
            anyString(), anyString(), anyString()))
            .thenReturn(Map.of(
                "message", "Registration successful!",
                "username", "testuser",
                "token", "mock-token"));

        Map<String, String> request = Map.of(
            "username", "testuser",
            "email", "test@novabank.com",
            "password", "pass123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper
                    .writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                    .value("Registration successful!"))
                .andExpect(jsonPath("$.username")
                    .value("testuser"));
    }

    @Test
    @DisplayName("Test 3 - Login Success")
    void login_returnsToken() throws Exception {
        when(userService.login(anyString(), anyString()))
            .thenReturn(Map.of(
                "message", "Login successful!",
                "username", "vinod",
                "token", "mock-token"));

        Map<String, String> request = Map.of(
            "username", "vinod",
            "password", "vinod123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper
                    .writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token")
                    .value("mock-token"))
                .andExpect(jsonPath("$.message")
                    .value("Login successful!"));
    }

    @Test
    @DisplayName("Test 4 - Login Invalid Credentials")
    void login_returnsUnauthorized() throws Exception {
        when(userService.login(anyString(), anyString()))
            .thenThrow(new RuntimeException(
                "Invalid username or password!"));

        Map<String, String> request = Map.of(
            "username", "wrong",
            "password", "wrong");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper
                    .writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error")
                    .value("Invalid username or password!"));
    }

    @Test
    @DisplayName("Test 5 - Register Missing Fields")
    void register_returnsBadRequest() throws Exception {
        Map<String, String> request = Map.of(
            "username", "testuser");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper
                    .writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}