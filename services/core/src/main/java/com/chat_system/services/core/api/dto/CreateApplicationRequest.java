package com.chat_system.services.core.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateApplicationRequest(
        @NotBlank
        @Size(min = 3, max = 25)
        String name
) {
}
