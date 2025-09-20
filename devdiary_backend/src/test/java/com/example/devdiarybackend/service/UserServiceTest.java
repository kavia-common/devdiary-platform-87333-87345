package com.example.devdiarybackend.service;

import com.example.devdiarybackend.domain.User;
import com.example.devdiarybackend.repository.UserRepository;
import com.example.devdiarybackend.web.dto.UserProfileResponse;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService.
 */
class UserServiceTest {

    @Test
    void getProfile_returnsProfile_whenUserExists() {
        UserRepository repo = mock(UserRepository.class);
        UserService svc = new UserService(repo);

        User u = new User();
        u.setId(10L);
        u.setEmail("a@b.com");
        u.setDisplayName("Alice");
        u.setActive(true);

        when(repo.findByEmail("a@b.com")).thenReturn(Optional.of(u));

        UserProfileResponse resp = svc.getProfile("a@b.com");

        assertEquals(10L, resp.id());
        assertEquals("a@b.com", resp.email());
        assertEquals("Alice", resp.displayName());
        assertTrue(resp.active());
    }
}
