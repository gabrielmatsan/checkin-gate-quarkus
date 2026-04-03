package com.checkingate.identity.infra.service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JwtTokenService {

    private static final Duration ACCESS_TOKEN_TTL = Duration.ofMinutes(15);
    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(7);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public String generateAccessToken(String userId, String role) {
        return Jwt.issuer("checkin-gate")
                .claim("user_id", userId)
                .claim("role", role)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(ACCESS_TOKEN_TTL))
                .sign();
    }

    public String generateRefreshToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public Duration getAccessTokenTTL() {
        return ACCESS_TOKEN_TTL;
    }

    public Duration getRefreshTokenTTL() {
        return REFRESH_TOKEN_TTL;
    }
}
