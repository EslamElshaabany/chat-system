package com.chat_system.services.core.infrastructure.persistence.adapter;

import com.chat_system.services.core.domain.interfaces.repository.ApplicationRepository;
import com.chat_system.services.core.domain.model.Application;
import com.chat_system.services.core.domain.model.ApplicationToken;
import com.chat_system.services.core.infrastructure.persistence.entity.ApplicationEntity;
import com.chat_system.services.core.infrastructure.persistence.repository.ApplicationJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ApplicationRepositoryAdapter implements ApplicationRepository {
    private final ApplicationJpaRepository applicationJpa;

    public ApplicationRepositoryAdapter(ApplicationJpaRepository jpa) {
        this.applicationJpa = jpa;
    }

    @Override
    public Application save(Application app) {
        var entity = toEntity(app);
        var saved = applicationJpa.save(entity);
        return toDomain(saved);
    }

    private ApplicationEntity toEntity(Application app) {
        return ApplicationEntity.builder()
                .id(app.id())
                .token(app.token().string())
                .name(app.name())
                .chatsCount(app.chatsCount())
                .createdAt(app.createdAt())
                .updatedAt(app.updatedAt())
                .build();
    }

    private Application toDomain(ApplicationEntity e) {
        return new Application(
                e.getId(),
                ApplicationToken.from(e.getToken()),
                e.getName(),
                e.getChatsCount(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

}
