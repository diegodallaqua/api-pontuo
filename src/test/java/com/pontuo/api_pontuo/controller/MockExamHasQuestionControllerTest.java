package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.service.MockExamHasQuestionService;
import com.pontuo.api_pontuo.support.WithApiSecurity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MockExamHasQuestionController.class)
@WithApiSecurity
@DisplayName("MockExamHasQuestionController")
class MockExamHasQuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MockExamHasQuestionService service;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private static JwtRequestPostProcessor estudante() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_ESTUDANTE"));
    }

    @Test
    @DisplayName("mockExamId tem precedência sobre questionId")
    void deveFiltrarSoPeloSimulado() throws Exception {
        mockMvc.perform(get("/api/mock-exam-questions").param("mockExamId", "1").param("questionId", "2")
                        .with(estudante()))
                .andExpect(status().isOk());

        verify(service).findByMockExamId(1L);
        verify(service, never()).findByQuestionId(any());
        verify(service, never()).findAll();
    }

    @Test
    @DisplayName("questionId sozinho filtra pela questão")
    void deveFiltrarPelaQuestao() throws Exception {
        mockMvc.perform(get("/api/mock-exam-questions").param("questionId", "2").with(estudante()))
                .andExpect(status().isOk());

        verify(service).findByQuestionId(2L);
        verify(service, never()).findAll();
    }

    @Test
    @DisplayName("GET de id inexistente responde 404")
    void deveResponder404ParaIdInexistente() throws Exception {
        when(service.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/mock-exam-questions/99").with(estudante()))
                .andExpect(status().isNotFound());
    }
}
