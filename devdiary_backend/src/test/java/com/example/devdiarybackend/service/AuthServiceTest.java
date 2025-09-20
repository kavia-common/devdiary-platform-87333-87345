package com.example.devdiarybackend.service;

import com.example.devdiarybackend.domain.User;
import com.example.devdiarybackend.repository.UserRepository;
import com.example.devdiarybackend.security.JwtUtil;
import com.example.devdiarybackend.security.JwtUtilProvider;
import com.example.devdiarybackend.web.dto.LoginRequest;
import com.example.devdiarybackend.web.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService registration and login logic.
 */
class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtilProvider jwtUtilProvider;
    private AuthService authService;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        JwtUtil jwtUtil = new JwtUtil("test-secret", 3600);
        jwtUtilProvider = mock(JwtUtilProvider.class);
        when(jwtUtilProvider.get()).thenReturn(jwtUtil);
        authService = new AuthService(userRepository, passwordEncoder, jwtUtilProvider);
    }

    @Test
    void register_createsUser_whenEmailNotExists() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("hashed");

        RegisterRequest req = new RegisterRequest("john@example.com", "John", "pass");
        User saved = new User();
        saved.setEmail("john@example.com");
        saved.setDisplayName("John");
        saved.setPasswordHash("hashed");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = authService.register(req);

        assertEquals("john@example.com", result.getEmail());
        assertEquals("John", result.getDisplayName());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("hashed", captor.getValue().getPasswordHash());
    }

    @Test
    void register_throwsWhenEmailExists() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(new User()));

        RegisterRequest req = new RegisterRequest("john@example.com", "John", "pass");
        assertThrows(IllegalArgumentException.class, () -> authService.register(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_returnsToken_whenCredentialsValid() {
        User u = new User();
        u.setEmail("john@example.com");
        u.setPasswordHash("hashed");
        u.setDisplayName("John");
        u.setActive(true);
        u.setId(1L);
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("pass", "hashed")).thenReturn(true);

        String token = authService.login(new LoginRequest("john@example.com", "pass"));
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void login_throws_whenInvalidPassword() {
        User u = new User();
        u.setEmail("john@example.com");
        u.setPasswordHash("hashed");
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.login(new LoginRequest("john@example.com", "wrong")));
    }

    @Test
    void currentUserFromToken_parsesToken() {
        User u = new User();
        u.setEmail("john@example.com");
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(u));

        // generate token
        String token = jwtUtilProvider.get().generateToken("john@example.com", java.util.Map.of());

        assertTrue(authService.currentUserFromToken(token).isPresent());
    }
}
