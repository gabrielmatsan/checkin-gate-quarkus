package com.checkingate.identity.application.usecase;

import java.time.Instant;

import com.checkingate.identity.application.dto.AuthOutput;
import com.checkingate.identity.domain.entity.Session;
import com.checkingate.identity.domain.entity.User;
import com.checkingate.identity.domain.port.SessionRepository;
import com.checkingate.identity.domain.port.UserRepository;
import com.checkingate.identity.infra.service.GoogleOAuthService;
import com.checkingate.identity.infra.service.JwtTokenService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AuthenticateWithGoogleUseCase {

    @Inject
    GoogleOAuthService googleOAuthService;

    @Inject
    JwtTokenService jwtTokenService;

    @Inject
    UserRepository userRepository;

    @Inject
    SessionRepository sessionRepository;

    @Transactional
    public AuthOutput execute(Input input) {
        var userInfo = googleOAuthService.exchangeAndGetUserInfo(input.code);

        User user = userRepository.findByEmail(userInfo.email()).orElse(null);

        if (user == null) {
            user = User.builder()
                .firstName(userInfo.givenName())
                .lastName(userInfo.familyName() != null ? userInfo.familyName() : "")
                .email(userInfo.email())
                .build();
            user = userRepository.save(user);
        }

        String accessToken = jwtTokenService.generateAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtTokenService.generateRefreshToken();

        Session session = Session.builder()
            .userId(user.getId())
            .refreshToken(refreshToken)
            .ipAddress(input.ipAddress)
            .userAgent(input.userAgent)
            .expiresAt(Instant.now().plus(jwtTokenService.getRefreshTokenTTL()))
            .build();
        sessionRepository.save(session);

        return new AuthOutput(
            accessToken,
            refreshToken,
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getRole().name()
        );
    }

    public record Input(String code, String ipAddress, String userAgent) {}
}
