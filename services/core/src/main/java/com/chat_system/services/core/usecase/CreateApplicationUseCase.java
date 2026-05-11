package com.chat_system.services.core.usecase;

import com.chat_system.services.core.domain.interfaces.repository.ApplicationRepository;
import com.chat_system.services.core.domain.model.Application;
import org.springframework.stereotype.Service;

@Service
public class CreateApplicationUseCase {
    private final ApplicationRepository applicationRepository;

    public CreateApplicationUseCase(ApplicationRepository repository) {
        this.applicationRepository = repository;
    }

    public Application execute(String name) {
        var app = Application.create(name);
        return applicationRepository.save(app);
    }
}
