package com.checkingate.identity.infra.web;

import com.checkingate.identity.application.usecase.AuthenticateWithGoogleUseCase;
import com.checkingate.identity.application.usecase.GetGoogleAuthUrlUseCase;
import com.checkingate.identity.application.usecase.RefreshTokenUseCase;

import jakarta.inject.Inject;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    GetGoogleAuthUrlUseCase getGoogleAuthUrlUseCase;

    @Inject
    AuthenticateWithGoogleUseCase authenticateWithGoogleUseCase;

    @Inject
    RefreshTokenUseCase refreshTokenUseCase;

    @GET
    @Path("/google/url")
    public Response getGoogleAuthUrl() {
        var output = getGoogleAuthUrlUseCase.execute();
        return Response.ok(output).build();
    }

    @GET
    @Path("/google/callback")
    public Response googleCallback(
            @QueryParam("code") String code,
            @QueryParam("state") String state,
            @Context HttpHeaders headers) {

        String ipAddress = headers.getHeaderString("X-Forwarded-For");
        if (ipAddress == null) ipAddress = "unknown";
        String userAgent = headers.getHeaderString("User-Agent");
        if (userAgent == null) userAgent = "unknown";

        var output = authenticateWithGoogleUseCase.execute(
            new AuthenticateWithGoogleUseCase.Input(code, ipAddress, userAgent)
        );

        NewCookie accessTokenCookie = new NewCookie.Builder("access_token")
                .value(output.accessToken())
                .path("/")
                .maxAge(900) // 15 minutes
                .httpOnly(true)
                .sameSite(NewCookie.SameSite.LAX)
                .build();

        NewCookie refreshTokenCookie = new NewCookie.Builder("refresh_token")
                .value(output.refreshToken())
                .path("/auth/refresh")
                .maxAge(604800) // 7 days
                .httpOnly(true)
                .sameSite(NewCookie.SameSite.STRICT)
                .build();

        return Response.ok(new CallbackResponse(
                output.userId(),
                output.email(),
                output.firstName(),
                output.lastName(),
                output.role()
        ))
        .cookie(accessTokenCookie)
        .cookie(refreshTokenCookie)
        .build();
    }

    @POST
    @Path("/refresh")
    public Response refreshToken(
            @CookieParam("refresh_token") String refreshToken,
            @Context HttpHeaders headers) {

        if (refreshToken == null || refreshToken.isBlank()) {
            return Response.status(401).entity(new ErrorResponse("missing refresh_token cookie")).build();
        }

        String ipAddress = headers.getHeaderString("X-Forwarded-For");
        if (ipAddress == null) ipAddress = "unknown";
        String userAgent = headers.getHeaderString("User-Agent");
        if (userAgent == null) userAgent = "unknown";

        var output = refreshTokenUseCase.execute(
            new RefreshTokenUseCase.Input(refreshToken, ipAddress, userAgent)
        );

        NewCookie accessTokenCookie = new NewCookie.Builder("access_token")
                .value(output.accessToken())
                .path("/")
                .maxAge(900)
                .httpOnly(true)
                .sameSite(NewCookie.SameSite.LAX)
                .build();

        NewCookie refreshTokenCookie = new NewCookie.Builder("refresh_token")
                .value(output.refreshToken())
                .path("/auth/refresh")
                .maxAge(604800)
                .httpOnly(true)
                .sameSite(NewCookie.SameSite.STRICT)
                .build();

        return Response.ok(new RefreshResponse("tokens refreshed"))
                .cookie(accessTokenCookie)
                .cookie(refreshTokenCookie)
                .build();
    }

    record CallbackResponse(String userId, String email, String firstName, String lastName, String role) {}
    record RefreshResponse(String message) {}
    record ErrorResponse(String error) {}
}
