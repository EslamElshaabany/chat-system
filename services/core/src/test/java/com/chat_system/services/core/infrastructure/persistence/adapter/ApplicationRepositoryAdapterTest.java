package com.chat_system.services.core.infrastructure.persistence.adapter;

import com.chat_system.services.core.domain.model.Application;
import com.chat_system.services.core.infrastructure.persistence.repository.ApplicationJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ApplicationRepositoryAdapter.class)
class ApplicationRepositoryAdapterTest {

    @Autowired
    ApplicationRepositoryAdapter adapter;

    @Autowired
    ApplicationJpaRepository jpaRepository;

    @Test
    void save_persistsAndReturnsApplicationWithGeneratedId() {
        var app = Application.create("MyApp");

        var saved = adapter.save(app);

        assertThat(saved.id()).isNotNull();
        assertThat(saved.name()).isEqualTo("MyApp");
        assertThat(saved.token()).isEqualTo(app.token());
        assertThat(saved.chatsCount()).isZero();
        assertThat(saved.createdAt()).isEqualTo(app.createdAt());
        assertThat(saved.updatedAt()).isEqualTo(app.updatedAt());
    }

    @Test
    void save_actuallyWritesToDatabase() {
        var app = Application.create("MyApp");

        var saved = adapter.save(app);

        var entity = jpaRepository.findById(saved.id()).orElseThrow();
        assertThat(entity.getName()).isEqualTo("MyApp");
        assertThat(entity.getToken()).isEqualTo(app.token().string());
        assertThat(entity.getChatsCount()).isZero();
    }

    @Test
    void save_tokenStoredAsString_roundTripsCorrectly() {
        var app = Application.create("MyApp");

        var saved = adapter.save(app);

        assertThat(saved.token()).isEqualTo(app.token());
    }
}
