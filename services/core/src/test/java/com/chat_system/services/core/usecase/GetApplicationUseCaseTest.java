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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetApplicationUseCaseTest {

    @Mock
    ApplicationRepository applicationRepository;

    GetApplicationUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetApplicationUseCase(applicationRepository);
    }

    @Test
    void execute_valid_token_return_valid_application() {
        var app = Application.create("MyApp");
        var token = app.token();

        when(applicationRepository.findBy(any()))
                .thenReturn(Optional.of(app));

        var result = useCase.execute(token);

        assertThat(result).isEqualTo(app);

    }

    @Test
    void execute_valid_token_pass_application_token_to_repository() {

        var app = Application.create("MyApp");
        var token = app.token();

        var captor = ArgumentCaptor.forClass(ApplicationToken.class);
        when(applicationRepository.findBy(captor.capture()))
                .thenReturn(Optional.of(app));

        useCase.execute(token);

        var captured = captor.getValue();
        assertThat(captured.value()).isEqualTo(token.value());
        assertThat(captured.string()).isEqualTo(token.string());
        assertThat(captured).isNotNull();
    }

    @Test
    void execute_token_for_not_existing_app_will_return_not_found() {
        var token = ApplicationToken.generate();

        when(applicationRepository.findBy(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(token))
                .isInstanceOf(ApplicationNotFoundException.class);

    }


}
