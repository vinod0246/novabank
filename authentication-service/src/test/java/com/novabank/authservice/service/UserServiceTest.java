package com.novabank.authservice.service;

import com.novabank.authservice.model.User;
import com.novabank.authservice.repository.UserRepository;
import com.novabank.authservice.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    private BCryptPasswordEncoder passwordEncoder =
        new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        lenient().when(jwtUtil.generateToken(anyString()))
            .thenReturn("mock-jwt-token");
    }

    @Test
    @DisplayName("Test 1 - Register Success")
    void testRegisterSuccess() {
        when(userRepository.existsByUsername("newuser"))
            .thenReturn(false);
        when(userRepository.existsByEmail("new@test.com"))
            .thenReturn(false);
        when(userRepository.save(any(User.class)))
            .thenAnswer(i -> i.getArgument(0));

        Map<String, String> result = userService.register(
            "newuser", "new@test.com", "pass123");

        assertEquals("Registration successful!",
            result.get("message"));
        assertEquals("newuser", result.get("username"));
        assertEquals("mock-jwt-token", result.get("token"));
    }

    @Test
    @DisplayName("Test 2 - Register Username Exists")
    void testRegisterUsernameExists() {
        when(userRepository.existsByUsername("existing"))
            .thenReturn(true);

        assertThrows(RuntimeException.class, () ->
            userService.register(
                "existing", "new@test.com", "pass123"));
    }

    @Test
    @DisplayName("Test 3 - Register Email Exists")
    void testRegisterEmailExists() {
        when(userRepository.existsByUsername("newuser"))
            .thenReturn(false);
        when(userRepository.existsByEmail(
                "existing@test.com"))
            .thenReturn(true);

        assertThrows(RuntimeException.class, () ->
            userService.register(
                "newuser", "existing@test.com", "pass123"));
    }

    @Test
    @DisplayName("Test 4 - Login Success")
    void testLoginSuccess() {
        User user = new User();
        user.setUsername("vinod");
        user.setPassword(
            passwordEncoder.encode("vinod123"));

        when(userRepository.findByUsername("vinod"))
            .thenReturn(Optional.of(user));

        Map<String, String> result =
            userService.login("vinod", "vinod123");

        assertEquals("Login successful!",
            result.get("message"));
        assertEquals("mock-jwt-token",
            result.get("token"));
    }

    @Test
    @DisplayName("Test 5 - Login Wrong Password")
    void testLoginWrongPassword() {
        User user = new User();
        user.setUsername("vinod");
        user.setPassword(
            passwordEncoder.encode("correctpass"));

        when(userRepository.findByUsername("vinod"))
            .thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () ->
            userService.login("vinod", "wrongpass"));
    }

    @Test
    @DisplayName("Test 6 - Login User Not Found")
    void testLoginUserNotFound() {
        when(userRepository.findByUsername("nobody"))
            .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
            userService.login("nobody", "pass123"));
    }
}