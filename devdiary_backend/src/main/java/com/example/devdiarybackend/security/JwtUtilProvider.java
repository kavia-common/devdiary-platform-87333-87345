package com.example.devdiarybackend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Provides configured JwtUtil instance based on environment variables.
 * Requires env vars:
 * - DEV_DIARY_JWT_SECRET
 * - DEV_DIARY_JWT_TTL_SECONDS
 */
@Configuration
public class JwtUtilProvider {
    private final JwtUtil jwtUtil;

    public JwtUtilProvider(
            @Value("${devdiary.jwt.secret:change-me}") String secret,
            @Value("${devdiary.jwt.ttl-seconds:86400}") long ttlSeconds
    ) {
        this.jwtUtil = new JwtUtil(secret, ttlSeconds);
    }

    public JwtUtil get() {
        return jwtUtil;
    }
}
