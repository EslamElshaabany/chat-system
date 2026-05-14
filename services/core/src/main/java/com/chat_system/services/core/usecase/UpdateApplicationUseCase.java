package com.chat_system.services.core.usecase;

import com.chat_system.services.core.domain.exception.ApplicationNotFoundException;
import com.chat_system.services.core.domain.interfaces.repository.ApplicationRepository;
import com.chat_system.services.core.domain.model.Application;
import com.chat_system.services.core.domain.model.ApplicationToken;
import org.springframework.stereotype.Service;

@Service
public class UpdateApplicationUseCase {
    private final ApplicationRepository applicationRepository;

    public UpdateApplicationUseCase(ApplicationRepository repository) {
        this.applicationRepository = repository;
    }

    public Application execute(ApplicationToken token, String name) {
        var app = applicationRepository.findBy(token)
                .orElseThrow(ApplicationNotFoundException::new);
        var updated = app.update(name);
        return applicationRepository.save(updated);
    }
}
