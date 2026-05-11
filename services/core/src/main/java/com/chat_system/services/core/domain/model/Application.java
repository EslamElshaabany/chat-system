package com.chat_system.services.core.domain.model;

import com.chat_system.services.core.domain.exception.InvalidApplicationException;

import java.time.Instant;
import java.util.UUID;

public record Application(
        Long id,
        UUID token,
        String name,
        int chatsCount,
        Instant createdAt,
        Instant updatedAt
) {

    public Application {
        if (name == null || name.isBlank())
            throw new InvalidApplicationException(InvalidApplicationException.NAME_BLANK);
        if (name.strip().length() < 3)
            throw new InvalidApplicationException(InvalidApplicationException.NAME_TOO_SHORT);
        if (name.strip().length() > 25)
            throw new InvalidApplicationException(InvalidApplicationException.NAME_TOO_LONG);

        name = name.strip();
    }

    public static Application create(String name) {
        return new Application(
                null,
                UUID.randomUUID(),
                name,
                0,
                Instant.now(),
                Instant.now()
        );
    }

}
