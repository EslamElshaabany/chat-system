package com.chat_system.services.core.usecase;

import com.chat_system.services.core.domain.exception.ApplicationNotFoundException;
import com.chat_system.services.core.domain.exception.InvalidApplicationException;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateApplicationUseCaseTest {

    @Mock
    ApplicationRepository applicationRepository;

    UpdateApplicationUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateApplicationUseCase(applicationRepository);
    }

    @Test
    void execute_validInput_savesAndReturnsUpdatedApplication() {
        var existing = Application.create("OldName");
        var token = existing.token();
        when(applicationRepository.findBy(token)).thenReturn(Optional.of(existing));
        when(applicationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = useCase.execute(token, "NewName");

        assertThat(result.name()).isEqualTo("NewName");
    }

    @Test
    void execute_validInput_passesUpdatedApplicationToRepository() {
        var existing = Application.create("OldName");
        var token = existing.token();
        when(applicationRepository.findBy(token)).thenReturn(Optional.of(existing));
        var captor = ArgumentCaptor.forClass(Application.class);
        when(applicationRepository.save(captor.capture())).thenAnswer(i -> i.getArgument(0));

        useCase.execute(token, "NewName");

        var captured = captor.getValue();
        assertThat(captured.name()).isEqualTo("NewName");
        assertThat(captured.token()).isEqualTo(existing.token());
    }

    @Test
    void execute_invalidName_throwsBeforeSaving() {
        var existing = Application.create("OldName");
        var token = existing.token();
        when(applicationRepository.findBy(token)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> useCase.execute(token, "ab"))
                .isInstanceOf(InvalidApplicationException.class);

        verify(applicationRepository, never()).save(any());
    }

    @Test
    void execute_applicationNotFound_throwsApplicationNotFoundException() {
        var token = ApplicationToken.generate();
        when(applicationRepository.findBy(token)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(token, "NewName"))
                .isInstanceOf(ApplicationNotFoundException.class);

        verify(applicationRepository, never()).save(any());
    }

    @Test
    void execute_returnsWhatRepositoryReturns() {
        var existing = Application.create("OldName");
        var token = existing.token();
        var saved = existing.update("NewName");
        when(applicationRepository.findBy(token)).thenReturn(Optional.of(existing));
        when(applicationRepository.save(any())).thenReturn(saved);

        var result = useCase.execute(token, "NewName");

        assertThat(result).isSameAs(saved);
    }
}
