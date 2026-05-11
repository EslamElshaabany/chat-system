package com.chat_system.services.core.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(T data, ErrorResponse error, Object meta) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data, null, null);
    }

    public static <T> ApiResponse<T> success(T data, Object meta) {
        return new ApiResponse<>(data, null, meta);
    }

    public static ApiResponse<?> failure(ErrorResponse error) {
        return new ApiResponse<>(null, error, null);
    }
}