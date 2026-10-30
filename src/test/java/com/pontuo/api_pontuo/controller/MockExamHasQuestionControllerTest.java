package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.entity.AnswerOption;
import com.pontuo.api_pontuo.entity.MockExam;
import com.pontuo.api_pontuo.entity.MockExamHasQuestion;
import com.pontuo.api_pontuo.entity.Question;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    private static JwtRequestPostProcessor administrador() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"));
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
    @DisplayName("GET de id inexistente responde 404 com mensagem")
    void deveResponder404ParaIdInexistente() throws Exception {
        when(service.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/mock-exam-questions/99").with(estudante()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("MockExamHasQuestion não encontrado: id=99"));
    }

    @Test
    @DisplayName("após finalizar, o estudante recebe a correção e a explicação")
    void deveCorrigirSemMostrarGabarito() throws Exception {
        MockExamHasQuestion link = respondida(true);
        link.getMockExam().setFinishedAt(LocalDateTime.now());
        when(service.findById(1L)).thenReturn(Optional.of(link));

        mockMvc.perform(get("/api/mock-exam-questions/1").with(estudante()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct").value(true))
                .andExpect(jsonPath("$.question.explanation").value("Explicação"))
                .andExpect(jsonPath("$.answerOption.letter").value("A"))
                .andExpect(jsonPath("$.answerOption.rightAnswer").doesNotExist());
    }

    @Test
    @DisplayName("antes de finalizar, o estudante não recebe correção nem explicação")
    void deveOcultarCorrecaoDuranteProva() throws Exception {
        when(service.findById(1L)).thenReturn(Optional.of(respondida(true)));

        mockMvc.perform(get("/api/mock-exam-questions/1").with(estudante()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct").doesNotExist())
                .andExpect(jsonPath("$.question.explanation").doesNotExist())
                .andExpect(jsonPath("$.answerOption.question.explanation").doesNotExist());
    }

    @Test
    @DisplayName("administrador pode revisar a correção antes da finalização")
    void deveMostrarCorrecaoAoAdministrador() throws Exception {
        when(service.findById(1L)).thenReturn(Optional.of(respondida(true)));

        mockMvc.perform(get("/api/mock-exam-questions/1").with(administrador()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct").value(true))
                .andExpect(jsonPath("$.question.explanation").value("Explicação"));
    }

    @Test
    @DisplayName("alternativa errada vira correct false")
    void deveMarcarErro() throws Exception {
        MockExamHasQuestion link = respondida(false);
        link.getMockExam().setFinishedAt(LocalDateTime.now());
        when(service.findById(1L)).thenReturn(Optional.of(link));

        mockMvc.perform(get("/api/mock-exam-questions/1").with(estudante()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct").value(false));
    }

    @Test
    @DisplayName("sem resposta correct fica nulo")
    void deveDeixarCorrectNuloSemResposta() throws Exception {
        MockExamHasQuestion pendente = new MockExamHasQuestion(simulado(), questao());
        when(service.findById(1L)).thenReturn(Optional.of(pendente));

        mockMvc.perform(get("/api/mock-exam-questions/1").with(estudante()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct").value(nullValue()))
                .andExpect(jsonPath("$.answerOption").value(nullValue()));
    }

    private static MockExamHasQuestion respondida(boolean acertou) {
        Question questao = questao();
        AnswerOption marcada = new AnswerOption("A", "Texto da alternativa", acertou, questao);
        marcada.setId(5L);
        return new MockExamHasQuestion(simulado(), questao, marcada, LocalDateTime.now());
    }

    private static MockExam simulado() {
        MockExam simulado = new MockExam("Simulado ENEM", (short) 10, (short) 90);
        simulado.setId(3L);
        return simulado;
    }

    private static Question questao() {
        Question questao = new Question("Enunciado", "Explicação", (short) 2);
        questao.setId(7L);
        return questao;
    }
}
