package com.checkingate.shared.exception;

public class BadRequestException extends DomainException {
    public BadRequestException(String message) {
        super(message, 400);
    }
}
