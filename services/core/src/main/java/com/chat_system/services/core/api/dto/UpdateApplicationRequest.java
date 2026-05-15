package com.chat_system.services.core.api.dto;

public record UpdateApplicationRequest(
        @ValidApplicationName
        String name
) {
}
