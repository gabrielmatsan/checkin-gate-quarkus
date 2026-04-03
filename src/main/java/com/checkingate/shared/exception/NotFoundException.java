package com.checkingate.shared.exception;

public class NotFoundException extends DomainException {
    public NotFoundException(String message) {
        super(message, 404);
    }
}
