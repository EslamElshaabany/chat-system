package com.chat_system.services.core.api.controller;

import com.chat_system.services.core.config.JacksonConfig;
import com.chat_system.services.core.domain.exception.ApplicationNotFoundException;
import com.chat_system.services.core.domain.exception.DomainException;
import com.chat_system.services.core.domain.exception.InvalidApplicationException;
import com.chat_system.services.core.domain.model.Application;
import com.chat_system.services.core.domain.model.ApplicationToken;
import com.chat_system.services.core.usecase.CreateApplicationUseCase;
import com.chat_system.services.core.usecase.GetApplicationUseCase;
import com.chat_system.services.core.usecase.UpdateApplicationUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @MockitoBean
    UpdateApplicationUseCase updateApplicationUseCase;

    private static final String VALID_TOKEN = "550e8400-e29b-41d4-a716-446655440000";

    @BeforeEach
    void setUp() {
        var app = Application.create("MyApp");
        when(createApplicationUseCase.execute("MyApp")).thenReturn(app);

        var updatedApp = Application.create("NewName");
        when(updateApplicationUseCase.execute(ApplicationToken.from(VALID_TOKEN), "NewName")).thenReturn(updatedApp);
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

    @Test
    void updateApplication_success() throws Exception {
        mockMvc.perform(put("/applications/" + VALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"NewName\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value("NewName"))
                .andExpect(jsonPath("$.data.chats_count").value(0))
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void updateApplication_tokenPassedToUseCase() throws Exception {
        var captor = ArgumentCaptor.forClass(ApplicationToken.class);
        when(updateApplicationUseCase.execute(captor.capture(), eq("NewName")))
                .thenReturn(Application.create("NewName"));

        mockMvc.perform(put("/applications/" + VALID_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"NewName\"}"));

        assertThat(captor.getValue().string()).isEqualTo(VALID_TOKEN);
    }

    @Test
    void updateApplication_wrongInputType_returns400() throws Exception {
        mockMvc.perform(put("/applications/" + VALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":123}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").isNotEmpty())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void updateApplication_nullName_returns422() throws Exception {
        mockMvc.perform(put("/applications/" + VALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":null}"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.error.message").isNotEmpty())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void updateApplication_nameTooShort_returns422() throws Exception {
        mockMvc.perform(put("/applications/" + VALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"ab\"}"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.error.message").isNotEmpty())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void updateApplication_domainExceptionFromUseCase_returns422() throws Exception {
        when(updateApplicationUseCase.execute(ApplicationToken.from(VALID_TOKEN), "NewName"))
                .thenThrow(new InvalidApplicationException(InvalidApplicationException.NAME_TOO_LONG));

        mockMvc.perform(put("/applications/" + VALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"NewName\"}"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.error.message").value(InvalidApplicationException.NAME_TOO_LONG))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void updateApplication_notFoundFromUseCase_returns404() throws Exception {
        when(updateApplicationUseCase.execute(ApplicationToken.from(VALID_TOKEN), "NewName"))
                .thenThrow(new ApplicationNotFoundException());

        mockMvc.perform(put("/applications/" + VALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"NewName\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message").isNotEmpty())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void updateApplication_invalidToken_returns400() throws Exception {
        mockMvc.perform(put("/applications/not-a-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"NewName\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").isNotEmpty())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void updateApplication_unmappedDomainException_returns500() throws Exception {
        when(updateApplicationUseCase.execute(ApplicationToken.from(VALID_TOKEN), "NewName"))
                .thenThrow(new DomainException("some unmapped domain error") {});

        mockMvc.perform(put("/applications/" + VALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"NewName\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.message").isNotEmpty())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void updateApplication_unexpectedException_returns500() throws Exception {
        when(updateApplicationUseCase.execute(ApplicationToken.from(VALID_TOKEN), "NewName"))
                .thenThrow(new RuntimeException("unexpected"));

        mockMvc.perform(put("/applications/" + VALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"NewName\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.message").isNotEmpty())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void updateApplication_missingBody_returns400() throws Exception {
        mockMvc.perform(put("/applications/" + VALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").isNotEmpty())
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
