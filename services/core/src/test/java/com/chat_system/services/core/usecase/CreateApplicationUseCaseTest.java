package com.chat_system.services.core.usecase;

import com.chat_system.services.core.domain.exception.InvalidApplicationException;
import com.chat_system.services.core.domain.interfaces.repository.ApplicationRepository;
import com.chat_system.services.core.domain.model.Application;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateApplicationUseCaseTest {

    @Mock
    ApplicationRepository applicationRepository;

    CreateApplicationUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateApplicationUseCase(applicationRepository);
    }

    @Test
    void execute_validName_savesAndReturnsApplication() {
        var saved = Application.create("MyApp");
        when(applicationRepository.save(any())).thenReturn(saved);

        var result = useCase.execute("MyApp");

        assertThat(result).isEqualTo(saved);
    }

    @Test
    void execute_validName_passesCreatedApplicationToRepository() {
        var captor = ArgumentCaptor.forClass(Application.class);
        when(applicationRepository.save(captor.capture())).thenAnswer(i -> i.getArgument(0));

        useCase.execute("MyApp");

        var captured = captor.getValue();
        assertThat(captured.name()).isEqualTo("MyApp");
        assertThat(captured.token()).isNotNull();
        assertThat(captured.id()).isNull();
    }

    @Test
    void execute_invalidName_throwsBeforeSaving() {
        assertThatThrownBy(() -> useCase.execute("ab"))
                .isInstanceOf(InvalidApplicationException.class);

        verifyNoInteractions(applicationRepository);
    }

    @Test
    void execute_returnsWhatRepositoryReturns() {
        var saved = Application.create("MyApp");
        when(applicationRepository.save(any())).thenReturn(saved);

        var result = useCase.execute("MyApp");

        assertThat(result).isSameAs(saved);
    }
}
