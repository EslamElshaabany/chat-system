package com.chat_system.services.core.infrastructure.persistence.repository;

import com.chat_system.services.core.infrastructure.persistence.entity.ApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationJpaRepository extends JpaRepository<ApplicationEntity, Long> {

}
