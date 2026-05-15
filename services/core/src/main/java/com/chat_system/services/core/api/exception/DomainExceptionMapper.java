package com.chat_system.services.core.api.exception;

import com.chat_system.services.core.domain.exception.ApplicationNotFoundException;
import com.chat_system.services.core.domain.exception.DomainException;
import com.chat_system.services.core.domain.exception.InvalidApplicationException;
import com.chat_system.services.core.domain.exception.InvalidApplicationTokenException;
import org.springframework.http.HttpStatus;

import java.util.Map;

public final class DomainExceptionMapper {

    private static final Map<Class<? extends DomainException>, HttpStatus> STATUS_MAP = Map.of(
            InvalidApplicationException.class, HttpStatus.UNPROCESSABLE_CONTENT,
            ApplicationNotFoundException.class, HttpStatus.NOT_FOUND,
            InvalidApplicationTokenException.class, HttpStatus.BAD_REQUEST
    );

    private DomainExceptionMapper() {
    }

    public static HttpStatus statusFor(DomainException ex) {
        return STATUS_MAP.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
