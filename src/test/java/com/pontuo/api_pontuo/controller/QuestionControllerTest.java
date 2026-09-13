package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.service.QuestionService;
import com.pontuo.api_pontuo.support.WithApiSecurity;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuestionController.class)
@WithApiSecurity
@DisplayName("QuestionController")
class QuestionControllerTest {

    private static final String QUESTAO_JSON = """
            {"statement": "Enunciado", "difficulty": 2, "entranceExamId": 1, "topicId": 2}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuestionService service;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private static JwtRequestPostProcessor administrador() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"));
    }

    @Nested
    @DisplayName("filtros da listagem")
    class Filtros {

        @Test
        @DisplayName("entranceExamId tem precedência e os outros filtros são ignorados")
        void deveUsarSoOVestibular() throws Exception {
            mockMvc.perform(get("/api/questions").param("entranceExamId", "1").param("topicId", "2")
                            .param("subjectId", "3").with(administrador()))
                    .andExpect(status().isOk());

            verify(service).findByEntranceExamId(1L);
            verify(service, never()).findByTopicId(any());
            verify(service, never()).findBySubjectId(any());
            verify(service, never()).findAll();
        }

        @Test
        @DisplayName("topicId vem antes de subjectId")
        void deveUsarTopicoAntesDaMateria() throws Exception {
            mockMvc.perform(get("/api/questions").param("topicId", "2").param("subjectId", "3").with(administrador()))
                    .andExpect(status().isOk());

            verify(service).findByTopicId(2L);
            verify(service, never()).findBySubjectId(any());
        }

        @Test
        @DisplayName("sem filtro lista todas as questões")
        void deveListarTudoSemFiltro() throws Exception {
            mockMvc.perform(get("/api/questions").with(administrador()))
                    .andExpect(status().isOk())
                    .andExpect(content().json("[]"));

            verify(service).findAll();
        }
    }

    @Nested
    @DisplayName("respostas de erro")
    class Erros {

        @Test
        @DisplayName("GET de id inexistente responde 404 sem corpo")
        void deveResponder404SemCorpo() throws Exception {
            when(service.findById(99L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/questions/99").with(administrador()))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(""));
        }

        @Test
        @DisplayName("filtro por tópico inexistente responde 404 com mensagem")
        void deveResponder404ParaFiltroInexistente() throws Exception {
            when(service.findByTopicId(99L)).thenThrow(new EntityNotFoundException("Topic não encontrado: id=99"));

            mockMvc.perform(get("/api/questions").param("topicId", "99").with(administrador()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Topic não encontrado: id=99"));
        }

        @Test
        @DisplayName("corpo inválido responde 400 por campo, sem chamar o serviço")
        void deveResponder400ParaCorpoInvalido() throws Exception {
            mockMvc.perform(post("/api/questions").with(administrador())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"statement\": \"\", \"difficulty\": 4}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.statement").value("statement é obrigatório"))
                    .andExpect(jsonPath("$.difficulty").value("difficulty deve estar entre 1 e 3"))
                    .andExpect(jsonPath("$.entranceExamId").value("entranceExamId é obrigatório"));

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("regra de negócio violada responde 409")
        void deveResponder409ParaRegraViolada() throws Exception {
            when(service.create(any(), eq(1L), eq(2L), isNull()))
                    .thenThrow(new IllegalArgumentException("informe topicId ou subjectId, não os dois"));

            mockMvc.perform(post("/api/questions").with(administrador())
                            .contentType(MediaType.APPLICATION_JSON).content(QUESTAO_JSON))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("informe topicId ou subjectId, não os dois"));
        }

        @Test
        @DisplayName("PUT de id inexistente responde 404 com mensagem")
        void deveResponder404NoUpdate() throws Exception {
            when(service.update(eq(99L), any(), anyLong(), anyLong(), isNull()))
                    .thenThrow(new EntityNotFoundException("Question não encontrada: id=99"));

            mockMvc.perform(put("/api/questions/99").with(administrador())
                            .contentType(MediaType.APPLICATION_JSON).content(QUESTAO_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Question não encontrada: id=99"));
        }
    }
}
