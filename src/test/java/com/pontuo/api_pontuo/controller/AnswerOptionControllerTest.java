package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.entity.AnswerOption;
import com.pontuo.api_pontuo.entity.Question;
import com.pontuo.api_pontuo.service.AnswerOptionService;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnswerOptionController.class)
@WithApiSecurity
@DisplayName("AnswerOptionController")
class AnswerOptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnswerOptionService service;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private static JwtRequestPostProcessor estudante() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_ESTUDANTE"));
    }

    private static JwtRequestPostProcessor administrador() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"));
    }

    private static AnswerOption alternativaCorreta() {
        Question questao = new Question("Enunciado", "Explicação", (short) 2);
        questao.setId(7L);
        AnswerOption alternativa = new AnswerOption("A", "Texto da alternativa", true, questao);
        alternativa.setId(1L);
        return alternativa;
    }

    @Test
    @DisplayName("estudante recebe a alternativa sem o gabarito")
    void naoDeveVazarGabaritoParaEstudante() throws Exception {
        when(service.findByQuestionId(7L)).thenReturn(List.of(alternativaCorreta()));

        mockMvc.perform(get("/api/answer-options").param("questionId", "7").with(estudante()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].letter").value("A"))
                .andExpect(jsonPath("$[0].text").value("Texto da alternativa"))
                .andExpect(jsonPath("$[0].rightAnswer").doesNotExist())
                .andExpect(jsonPath("$[0].question.explanation").doesNotExist());
    }

    @Test
    @DisplayName("administrador recebe o gabarito, que ele precisa para revisar o cadastro")
    void deveMostrarGabaritoParaAdministrador() throws Exception {
        when(service.findByQuestionId(7L)).thenReturn(List.of(alternativaCorreta()));

        mockMvc.perform(get("/api/answer-options").param("questionId", "7").with(administrador()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rightAnswer").value(true))
                .andExpect(jsonPath("$[0].question.explanation").value("Explicação"));
    }

    @Test
    @DisplayName("GET de id também esconde o gabarito do estudante")
    void naoDeveVazarGabaritoNoGetPorId() throws Exception {
        when(service.findById(1L)).thenReturn(Optional.of(alternativaCorreta()));

        mockMvc.perform(get("/api/answer-options/1").with(estudante()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rightAnswer").doesNotExist());
    }
}
