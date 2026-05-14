package com.chat_system.services.core.domain.exception;

public class InvalidApplicationTokenException extends DomainException {
    public InvalidApplicationTokenException(String message) {
        super(message);
    }
}
