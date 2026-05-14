package com.chat_system.services.core.usecase;

import com.chat_system.services.core.domain.exception.ApplicationNotFoundException;
import com.chat_system.services.core.domain.interfaces.repository.ApplicationRepository;
import com.chat_system.services.core.domain.model.Application;
import com.chat_system.services.core.domain.model.ApplicationToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetApplicationUseCaseTest {

    @Mock
    ApplicationRepository applicationRepository;

    GetApplicationUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetApplicationUseCase(applicationRepository);
    }

    @Test
    void execute_existingToken_returnsApplication() {
        var app = Application.create("MyApp");
        when(applicationRepository.findBy(any())).thenReturn(Optional.of(app));

        var result = useCase.execute(app.token());

        assertThat(result).isEqualTo(app);
    }

    @Test
    void execute_passesTokenToRepository() {
        var app = Application.create("MyApp");
        var token = app.token();
        var captor = ArgumentCaptor.forClass(ApplicationToken.class);
        when(applicationRepository.findBy(captor.capture())).thenReturn(Optional.of(app));

        useCase.execute(token);

        assertThat(captor.getValue()).isEqualTo(token);
    }

    @Test
    void execute_nonExistingToken_throwsApplicationNotFoundException() {
        when(applicationRepository.findBy(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(ApplicationToken.generate()))
                .isInstanceOf(ApplicationNotFoundException.class);
    }
}
