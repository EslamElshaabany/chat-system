package com.chat_system.services.core.api.exception;

import com.chat_system.services.core.api.dto.ApiResponse;
import com.chat_system.services.core.api.dto.ErrorResponse;
import com.chat_system.services.core.api.dto.FieldError;
import com.chat_system.services.core.domain.exception.DomainException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<?>> handleDomain(DomainException ex) {
        var status = DomainExceptionMapper.statusFor(ex);
        return ResponseEntity.status(status).body(fail(ex.getMessage()));
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleBadRequest() {
        return fail(ErrorMessages.MALFORMED_REQUEST);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<?> handleNotFound() {
        return fail(ErrorMessages.RESOURCE_NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public ApiResponse<?> handleValidation(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new FieldError(
                        e.getField(),
                        Objects.requireNonNullElse(e.getDefaultMessage(), "invalid")
                ))
                .toList();
        return ApiResponse.failure(new ErrorResponse<>(ErrorMessages.INVALID_INPUT, errors));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<?> handleUnexpected(Exception ex) {
        log.error("Unhandled exception", ex);
        return fail(ErrorMessages.SOMETHING_WENT_WRONG);
    }

    private ApiResponse<?> fail(String message) {
        return ApiResponse.failure(new ErrorResponse<>(message, null));
    }
}