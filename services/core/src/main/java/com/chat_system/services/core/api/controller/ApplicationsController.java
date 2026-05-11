package com.chat_system.services.core.api.controller;

import com.chat_system.services.core.api.dto.ApplicationResponse;
import com.chat_system.services.core.api.dto.CreateApplicationRequest;
import com.chat_system.services.core.usecase.CreateApplicationUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applications")
public class ApplicationsController {

    private final CreateApplicationUseCase createApplicationUseCase;

    public ApplicationsController(CreateApplicationUseCase createApplicationUseCase) {
        this.createApplicationUseCase = createApplicationUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ApplicationResponse create(@Valid @RequestBody CreateApplicationRequest req) {
        var app = createApplicationUseCase.execute(req.name());
        return ApplicationResponse.from(app);
    }
}
