package com.chat_system.services.core.domain.exception;

public class InvalidApplicationException extends DomainException {
    public static final String NAME_BLANK = "Application name must not be blank";
    public static final String NAME_TOO_SHORT = "Application name must be at least 3 characters";
    public static final String NAME_TOO_LONG = "Application name must not exceed 25 characters";

    public InvalidApplicationException(String message) {
        super(message);
    }
}
