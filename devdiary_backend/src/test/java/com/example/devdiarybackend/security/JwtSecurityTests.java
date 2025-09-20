package com.example.devdiarybackend.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for JWT utility and authentication filter.
 */
class JwtSecurityTests {

    @Test
    void jwtUtil_generateAndParse_roundTrip() {
        JwtUtil util = new JwtUtil("secret", 60);
        String token = util.generateToken("user@example.com", Map.of("roles", java.util.List.of("USER")));
        Claims c = util.parse(token);
        assertEquals("user@example.com", c.getSubject());
        assertNotNull(c.getExpiration());
    }

    @Test
    void jwtAuthFilter_setsAuthentication_onValidToken() throws ServletException, IOException {
        JwtUtilProvider provider = mock(JwtUtilProvider.class);
        JwtUtil util = new JwtUtil("secret", 60);
        when(provider.get()).thenReturn(util);

        JwtAuthFilter filter = new JwtAuthFilter(provider);
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        String token = util.generateToken("u@e.com", Map.of("roles", java.util.List.of("USER")));
        when(req.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);

        // Clear context before
        SecurityContextHolder.clearContext();

        filter.doFilterInternal(req, res, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("u@e.com", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(chain, times(1)).doFilter(req, res);
    }

    @Test
    void jwtAuthFilter_ignores_whenInvalidOrMissing() throws ServletException, IOException {
        JwtUtilProvider provider = mock(JwtUtilProvider.class);
        JwtUtil util = new JwtUtil("secret", 60);
        when(provider.get()).thenReturn(util);

        JwtAuthFilter filter = new JwtAuthFilter(provider);
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(req.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer invalid.token.here");

        SecurityContextHolder.clearContext();
        filter.doFilterInternal(req, res, chain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        when(req.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        SecurityContextHolder.clearContext();
        filter.doFilterInternal(req, res, chain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
