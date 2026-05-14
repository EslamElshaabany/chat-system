package com.chat_system.services.core.domain.model;

import com.chat_system.services.core.domain.exception.InvalidApplicationTokenException;

import java.util.UUID;

public record ApplicationToken(UUID value) {

    public static ApplicationToken generate() {
        return new ApplicationToken(UUID.randomUUID());
    }

    public static ApplicationToken from(String tokenString) {
        return new ApplicationToken(validate(tokenString));
    }

    private static UUID validate(String tokenString) {
        if (tokenString == null)
            throw new InvalidApplicationTokenException("Token must not be null");
        try {
            return UUID.fromString(tokenString);
        } catch (IllegalArgumentException e) {
            throw new InvalidApplicationTokenException(e.getMessage());
        }
    }

    public String string() {
        return this.value().toString();
    }

}
