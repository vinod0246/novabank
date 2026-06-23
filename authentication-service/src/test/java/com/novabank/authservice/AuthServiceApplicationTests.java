package com.novabank.authservice;

import com.novabank.authservice.model.User;
import com.novabank.authservice.repository.UserRepository;
import com.novabank.authservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthServiceApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Test 1 - Context Loads")
    void contextLoads() {
    }

    @Test
    @DisplayName("Test 2 - Register User Successfully")
    void testRegisterUser() {
        Map<String, String> response = userService.register(
            "testuser", "test@novabank.com", "password123");

        assertNotNull(response);
        assertEquals("Registration successful!",
            response.get("message"));
        assertEquals("testuser", response.get("username"));
        assertNotNull(response.get("token"));
    }

    @Test
    @DisplayName("Test 3 - Register Duplicate Username")
    void testRegisterDuplicateUsername() {
        userService.register(
            "vinod", "vinod@novabank.com", "vinod123");

        assertThrows(RuntimeException.class, () ->
            userService.register(
                "vinod", "other@novabank.com", "pass123"));
    }

    @Test
    @DisplayName("Test 4 - Register Duplicate Email")
    void testRegisterDuplicateEmail() {
        userService.register(
            "user1", "same@novabank.com", "pass123");

        assertThrows(RuntimeException.class, () ->
            userService.register(
                "user2", "same@novabank.com", "pass456"));
    }

    @Test
    @DisplayName("Test 5 - Login Successfully")
    void testLoginSuccess() {
        userService.register(
            "loginuser", "login@novabank.com", "pass123");

        Map<String, String> response = userService.login(
            "loginuser", "pass123");

        assertNotNull(response);
        assertEquals("Login successful!",
            response.get("message"));
        assertNotNull(response.get("token"));
    }

    @Test
    @DisplayName("Test 6 - Login Wrong Password")
    void testLoginWrongPassword() {
        userService.register(
            "wrongpass", "wrong@novabank.com", "correct123");

        assertThrows(RuntimeException.class, () ->
            userService.login("wrongpass", "wrongpassword"));
    }

    @Test
    @DisplayName("Test 7 - Login Non Existent User")
    void testLoginNonExistentUser() {
        assertThrows(RuntimeException.class, () ->
            userService.login("nobody", "pass123"));
    }

    @Test
    @DisplayName("Test 8 - Password Is Encrypted")
    void testPasswordEncrypted() {
        userService.register(
            "encryptuser",
            "encrypt@novabank.com",
            "plainpassword");

        User user = userRepository
            .findByUsername("encryptuser")
            .orElseThrow();

        assertNotEquals("plainpassword", user.getPassword());
        assertTrue(user.getPassword().startsWith("$2a$"));
    }
}