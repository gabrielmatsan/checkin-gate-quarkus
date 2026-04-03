package com.checkingate.identity.application.usecase;

import com.checkingate.identity.infra.service.GoogleOAuthService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetGoogleAuthUrlUseCase {

    @Inject
    GoogleOAuthService googleOAuthService;

    public Output execute() {
        var result = googleOAuthService.getAuthUrl();
        return new Output(result.url(), result.state());
    }

    public record Output(String url, String state) {}
}
