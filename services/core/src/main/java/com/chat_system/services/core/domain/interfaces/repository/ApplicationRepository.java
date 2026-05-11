package com.chat_system.services.core.domain.interfaces.repository;

import com.chat_system.services.core.domain.model.Application;

public interface ApplicationRepository {
    Application save(Application application);
}
