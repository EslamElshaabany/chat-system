package com.chat_system.services.core.domain.exception;

public class ApplicationNotFoundException extends DomainException {
    public ApplicationNotFoundException() {
        super("Application Not Found");
    }
}
