package com.chat_system.services.core.usecase;

import com.chat_system.services.core.domain.interfaces.repository.ApplicationRepository;
import com.chat_system.services.core.domain.model.Application;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class CreateApplicationUseCase {
    private final ApplicationRepository applicationRepository;
    private final Counter applicationsCreated;

    public CreateApplicationUseCase(ApplicationRepository repository, MeterRegistry meterRegistry) {
        this.applicationRepository = repository;
        this.applicationsCreated = meterRegistry.counter("chat.applications.created");
    }

    public Application execute(String name) {
        var saved = applicationRepository.save(Application.create(name));
        applicationsCreated.increment();
        return saved;
    }
}
