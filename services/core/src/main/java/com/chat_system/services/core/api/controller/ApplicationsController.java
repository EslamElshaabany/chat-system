package com.chat_system.services.core.api.controller;

import com.chat_system.services.core.api.dto.ApplicationResponse;
import com.chat_system.services.core.api.dto.CreateApplicationRequest;
import com.chat_system.services.core.domain.model.ApplicationToken;
import com.chat_system.services.core.usecase.CreateApplicationUseCase;
import com.chat_system.services.core.usecase.GetApplicationUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applications")
@Validated
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
    @ResponseStatus(HttpStatus.OK)
    public ApplicationResponse get(@PathVariable @NotBlank @Size(min = 36, max = 36) String token) {
        var app = getApplicationUseCase.execute(ApplicationToken.from(token.strip()));
        return ApplicationResponse.from(app);
    }
}
