package com.checkingate.identity.application.dto;

public record AuthOutput(
    String accessToken,
    String refreshToken,
    String userId,
    String email,
    String firstName,
    String lastName,
    String role
) {}
