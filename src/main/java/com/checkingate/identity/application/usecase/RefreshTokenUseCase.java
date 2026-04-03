package com.checkingate.identity.application.usecase;

import java.time.Instant;

import com.checkingate.identity.domain.entity.Session;
import com.checkingate.identity.domain.port.SessionRepository;
import com.checkingate.identity.domain.port.UserRepository;
import com.checkingate.identity.infra.service.JwtTokenService;
import com.checkingate.shared.exception.BadRequestException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class RefreshTokenUseCase {

    @Inject
    JwtTokenService jwtTokenService;

    @Inject
    UserRepository userRepository;

    @Inject
    SessionRepository sessionRepository;

    @Transactional
    public Output execute(Input input) {
        var session = sessionRepository.findByRefreshToken(input.refreshToken)
                .orElseThrow(() -> new BadRequestException("invalid refresh token"));

        if (session.isExpired()) {
            sessionRepository.delete(session.getId());
            throw new BadRequestException("session expired");
        }

        var user = userRepository.findById(session.getUserId())
                .orElseThrow(() -> new BadRequestException("invalid refresh token"));

        String accessToken = jwtTokenService.generateAccessToken(user.getId(), user.getRole().name());
        String newRefreshToken = jwtTokenService.generateRefreshToken();

        // Rotate: delete old session, create new
        sessionRepository.delete(session.getId());

        Session newSession = Session.builder()
            .userId(user.getId())
            .refreshToken(newRefreshToken)
            .ipAddress(input.ipAddress)
            .userAgent(input.userAgent)
            .expiresAt(Instant.now().plus(jwtTokenService.getRefreshTokenTTL()))
            .build();
        sessionRepository.save(newSession);

        return new Output(accessToken, newRefreshToken);
    }

    public record Input(String refreshToken, String ipAddress, String userAgent) {}
    public record Output(String accessToken, String refreshToken) {}
}
