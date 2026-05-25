package com.chat_system.services.core.usecase;

import com.chat_system.services.core.domain.exception.ApplicationNotFoundException;
import com.chat_system.services.core.domain.interfaces.repository.ApplicationRepository;
import com.chat_system.services.core.domain.model.Application;
import com.chat_system.services.core.domain.model.ApplicationToken;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class UpdateApplicationUseCase {
    private final ApplicationRepository applicationRepository;
    private final Counter applicationsUpdated;

    public UpdateApplicationUseCase(ApplicationRepository repository, MeterRegistry meterRegistry) {
        this.applicationRepository = repository;
        this.applicationsUpdated = meterRegistry.counter("chat.applications.updated");
    }

    public Application execute(ApplicationToken token, String name) {
        var app = applicationRepository.findBy(token)
                .orElseThrow(ApplicationNotFoundException::new);
        var saved = applicationRepository.save(app.update(name));
        applicationsUpdated.increment();
        return saved;
    }
}
