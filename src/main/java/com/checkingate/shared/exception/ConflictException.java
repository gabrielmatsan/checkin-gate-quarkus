package com.checkingate.shared.exception;

public class ConflictException extends DomainException {
    public ConflictException(String message) {
        super(message, 409);
    }
}
