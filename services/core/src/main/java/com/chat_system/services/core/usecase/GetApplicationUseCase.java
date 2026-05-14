package com.chat_system.services.core.usecase;

import com.chat_system.services.core.domain.exception.ApplicationNotFoundException;
import com.chat_system.services.core.domain.interfaces.repository.ApplicationRepository;
import com.chat_system.services.core.domain.model.Application;
import com.chat_system.services.core.domain.model.ApplicationToken;
import org.springframework.stereotype.Service;

@Service
public class GetApplicationUseCase {

    private final ApplicationRepository applicationRepository;

    public GetApplicationUseCase(ApplicationRepository repository) {
        this.applicationRepository = repository;
    }

    public Application execute(ApplicationToken token) {
        return applicationRepository
                .findBy(token)
                .orElseThrow(ApplicationNotFoundException::new);
    }
}
