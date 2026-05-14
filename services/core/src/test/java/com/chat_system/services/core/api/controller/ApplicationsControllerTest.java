package com.chat_system.services.core.api.controller;

import com.chat_system.services.core.config.JacksonConfig;
import com.chat_system.services.core.domain.exception.ApplicationNotFoundException;
import com.chat_system.services.core.domain.exception.DomainException;
import com.chat_system.services.core.domain.exception.InvalidApplicationException;
import com.chat_system.services.core.domain.model.Application;
import com.chat_system.services.core.domain.model.ApplicationToken;
import com.chat_system.services.core.usecase.CreateApplicationUseCase;
import com.chat_system.services.core.usecase.GetApplicationUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApplicationsController.class)
@Import(JacksonConfig.class)
public class ApplicationsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CreateApplicationUseCase createApplicationUseCase;

    @MockitoBean
    GetApplicationUseCase getApplicationUseCase;

    @BeforeEach
    void setUp() {
        var app = Application.create("MyApp");
        when(createApplicationUseCase.execute("MyApp")).thenReturn(app);
    }

    @Test
    void createApplication_success() throws Exception {
        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"MyApp\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value("MyApp"))
                .andExpect(jsonPath("$.data.chats_count").value(0))
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void createApplication_wrongInputType_returns400() throws Exception {
        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":123}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").isNotEmpty())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void createApplication_invalidInput_returns422() throws Exception {
        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"ab\"}"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.error.message").isNotEmpty())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void createApplication_domainExceptionFromUseCase_returns422() throws Exception {
        when(createApplicationUseCase.execute("MyApp"))
                .thenThrow(new InvalidApplicationException(InvalidApplicationException.NAME_TOO_LONG));

        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"MyApp\"}"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.error.message").value(InvalidApplicationException.NAME_TOO_LONG))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void createApplication_unmappedDomainException_returns500() throws Exception {
        when(createApplicationUseCase.execute("MyApp"))
                .thenThrow(new DomainException("some unmapped domain error") {
                });

        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"MyApp\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.message").isNotEmpty())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void createApplication_unexpectedException_returns500() throws Exception {
        when(createApplicationUseCase.execute("MyApp"))
                .thenThrow(new RuntimeException("unexpected"));

        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"MyApp\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.message").isNotEmpty())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void createApplication_nullName_returns422() throws Exception {
        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":null}"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.error.message").isNotEmpty())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void createApplication_missingBody_returns400() throws Exception {
        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").isNotEmpty())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getApplication_success() throws Exception {
        var app = Application.create("MyApp");
        when(getApplicationUseCase.execute(any())).thenReturn(app);

        mockMvc.perform(get("/applications/" + app.token().string()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value(app.token().string()))
                .andExpect(jsonPath("$.data.name").value("MyApp"))
                .andExpect(jsonPath("$.data.chats_count").value(0))
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void getApplication_notFound_returns404() throws Exception {
        when(getApplicationUseCase.execute(any())).thenThrow(new ApplicationNotFoundException());

        mockMvc.perform(get("/applications/" + Application.create("MyApp").token().string()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message").isNotEmpty())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getApplication_passesCorrectTokenToUseCase() throws Exception {
        var app = Application.create("MyApp");
        var token = app.token();
        var captor = ArgumentCaptor.forClass(ApplicationToken.class);
        when(getApplicationUseCase.execute(captor.capture())).thenReturn(app);

        mockMvc.perform(get("/applications/" + token.string()));

        assertThat(captor.getValue().string()).isEqualTo(token.string());
    }

    @Test
    void getApplication_invalidToken_returns400() throws Exception {
        mockMvc.perform(get("/applications/not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").isNotEmpty())
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
