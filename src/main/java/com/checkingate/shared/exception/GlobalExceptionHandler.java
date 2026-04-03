package com.checkingate.shared.exception;

import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

public class GlobalExceptionHandler {

    @ServerExceptionMapper(DomainException.class)
    public Response handleDomainException(DomainException e) {
        return Response.status(e.getStatusCode())
                .entity(new ErrorResponse(e.getMessage()))
                .build();
    }

    public record ErrorResponse(String error) {}
}
