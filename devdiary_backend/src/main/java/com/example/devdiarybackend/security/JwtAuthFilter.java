package com.example.devdiarybackend.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Extracts JWT from Authorization header and authenticates request.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtilProvider jwtUtilProvider;

    public JwtAuthFilter(JwtUtilProvider jwtUtilProvider) {
        this.jwtUtilProvider = jwtUtilProvider;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                Claims claims = jwtUtilProvider.get().parse(token);
                String subject = claims.getSubject();
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) claims.getOrDefault("roles", List.of("USER"));
                List<SimpleGrantedAuthority> authorities = roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)).toList();
                Authentication authentication = new AbstractAuthenticationToken(authorities) {
                    @Override
                    public Object getCredentials() { return token; }

                    @Override
                    public Object getPrincipal() { return subject; }

                    @Override
                    public boolean isAuthenticated() { return true; }
                };
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception ignored) {
                // invalid token -> proceed unauthenticated
            }
        }
        filterChain.doFilter(request, response);
    }
}
