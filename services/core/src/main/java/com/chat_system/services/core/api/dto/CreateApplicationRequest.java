package com.chat_system.services.core.api.dto;

public record CreateApplicationRequest(
        @ValidApplicationName
        String name
) {
}
