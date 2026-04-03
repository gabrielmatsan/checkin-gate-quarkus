package com.checkingate.identity.infra.service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class GoogleOAuthService {

    @ConfigProperty(name = "google.client-id")
    String clientId;

    @ConfigProperty(name = "google.client-secret")
    String clientSecret;

    @ConfigProperty(name = "google.redirect-url")
    String redirectUrl;

    @Inject
    ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public AuthUrlResult getAuthUrl() {
        String state = UUID.randomUUID().toString();
        String url = "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + encode(clientId)
                + "&redirect_uri=" + encode(redirectUrl)
                + "&response_type=code"
                + "&scope=" + encode("openid email profile")
                + "&state=" + encode(state)
                + "&access_type=offline";
        return new AuthUrlResult(url, state);
    }

    public GoogleUserInfo exchangeAndGetUserInfo(String code) {
        try {
            // Exchange code for token
            String tokenBody = "code=" + encode(code)
                    + "&client_id=" + encode(clientId)
                    + "&client_secret=" + encode(clientSecret)
                    + "&redirect_uri=" + encode(redirectUrl)
                    + "&grant_type=authorization_code";

            HttpRequest tokenRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://oauth2.googleapis.com/token"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(tokenBody))
                    .build();

            HttpResponse<String> tokenResponse = httpClient.send(tokenRequest, HttpResponse.BodyHandlers.ofString());
            TokenResponse token = objectMapper.readValue(tokenResponse.body(), TokenResponse.class);

            // Get user info
            HttpRequest userInfoRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.googleapis.com/oauth2/v3/userinfo"))
                    .header("Authorization", "Bearer " + token.accessToken)
                    .GET()
                    .build();

            HttpResponse<String> userInfoResponse = httpClient.send(userInfoRequest, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(userInfoResponse.body(), GoogleUserInfo.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to exchange Google OAuth code", e);
        }
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    public record AuthUrlResult(String url, String state) {}

    public record GoogleUserInfo(
        String sub,
        String email,
        @JsonProperty("given_name") String givenName,
        @JsonProperty("family_name") String familyName
    ) {}

    private record TokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") int expiresIn
    ) {}
}
