package com.chat_system.services.core.domain.model;

import com.chat_system.services.core.domain.exception.InvalidApplicationException;

import java.time.Instant;

public record Application(
        Long id,
        ApplicationToken token,
        String name,
        int chatsCount,
        Instant createdAt,
        Instant updatedAt
) {
    public static Application create(String name) {
        return new Application(
                null,
                ApplicationToken.generate(),
                validateName(name),
                0,
                Instant.now(),
                Instant.now()
        );
    }

    public Application update(String name) {
        return new Application(
                this.id,
                this.token,
                validateName(name),
                this.chatsCount,
                this.createdAt,
                Instant.now()
        );
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank())
            throw new InvalidApplicationException(InvalidApplicationException.NAME_BLANK);
        if (name.strip().length() < 3)
            throw new InvalidApplicationException(InvalidApplicationException.NAME_TOO_SHORT);
        if (name.strip().length() > 25)
            throw new InvalidApplicationException(InvalidApplicationException.NAME_TOO_LONG);
        return name.strip();
    }
}