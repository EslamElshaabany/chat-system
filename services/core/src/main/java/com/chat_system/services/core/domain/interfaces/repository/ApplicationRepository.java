package com.chat_system.services.core.domain.interfaces.repository;

import com.chat_system.services.core.domain.model.Application;
import com.chat_system.services.core.domain.model.ApplicationToken;

import java.util.Optional;

public interface ApplicationRepository {

    Application save(Application application);

    Optional<Application> findBy(ApplicationToken token);

}
