package com.chat_system.services.core.api.controller;

import com.chat_system.services.core.api.dto.ApplicationResponse;
import com.chat_system.services.core.api.dto.CreateApplicationRequest;
import com.chat_system.services.core.domain.model.ApplicationToken;
import com.chat_system.services.core.usecase.CreateApplicationUseCase;
import com.chat_system.services.core.usecase.GetApplicationUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applications")
public class ApplicationsController {

    private final CreateApplicationUseCase createApplicationUseCase;
    private final GetApplicationUseCase getApplicationUseCase;

    public ApplicationsController(
            CreateApplicationUseCase createApplicationUseCase,
            GetApplicationUseCase getApplicationUseCase
    ) {
        this.createApplicationUseCase = createApplicationUseCase;
        this.getApplicationUseCase = getApplicationUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationResponse create(@Valid @RequestBody CreateApplicationRequest req) {
        var app = createApplicationUseCase.execute(req.name());
        return ApplicationResponse.from(app);
    }

    @GetMapping("/{token}")
    public ApplicationResponse get(@PathVariable String token) {
        var app = getApplicationUseCase.execute(ApplicationToken.from(token));
        return ApplicationResponse.from(app);
    }
}
