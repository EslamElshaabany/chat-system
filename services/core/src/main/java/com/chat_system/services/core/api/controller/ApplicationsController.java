package com.chat_system.services.core.api.controller;

import com.chat_system.services.core.api.dto.ApplicationResponse;
import com.chat_system.services.core.api.dto.CreateApplicationRequest;
import com.chat_system.services.core.api.dto.UpdateApplicationRequest;
import com.chat_system.services.core.domain.model.ApplicationToken;
import com.chat_system.services.core.usecase.CreateApplicationUseCase;
import com.chat_system.services.core.usecase.GetApplicationUseCase;
import com.chat_system.services.core.usecase.UpdateApplicationUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applications")
public class ApplicationsController {

    private final CreateApplicationUseCase createApplicationUseCase;
    private final GetApplicationUseCase getApplicationUseCase;
    private final UpdateApplicationUseCase updateApplicationUseCase;

    public ApplicationsController(
            CreateApplicationUseCase createApplicationUseCase,
            GetApplicationUseCase getApplicationUseCase,
            UpdateApplicationUseCase updateApplicationUseCase
    ) {
        this.createApplicationUseCase = createApplicationUseCase;
        this.getApplicationUseCase = getApplicationUseCase;
        this.updateApplicationUseCase = updateApplicationUseCase;
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

    @PutMapping("/{token}")
    public ApplicationResponse update(@PathVariable String token,
                                      @Valid @RequestBody UpdateApplicationRequest req) {
        var app = updateApplicationUseCase.execute(ApplicationToken.from(token), req.name());
        return ApplicationResponse.from(app);
    }
}
