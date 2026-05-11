package com.chat_system.services.core.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateApplicationRequest(
        // ToDo: fix accepting wrong types
        @NotBlank
        @Size(min = 3, max = 35)
        String name
) {
}
