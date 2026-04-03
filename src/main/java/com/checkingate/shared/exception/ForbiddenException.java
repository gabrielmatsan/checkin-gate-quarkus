package com.checkingate.shared.exception;

public class ForbiddenException extends DomainException {
    public ForbiddenException(String message) {
        super(message, 403);
    }
}
