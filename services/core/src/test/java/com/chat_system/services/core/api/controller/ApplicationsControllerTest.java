package com.chat_system.services.core.api.controller;

import com.chat_system.services.core.config.JacksonConfig;
import com.chat_system.services.core.domain.model.Application;
import com.chat_system.services.core.usecase.CreateApplicationUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
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
}
