package com.pontuo.api_pontuo.config;

import com.pontuo.api_pontuo.controller.AuthController;
import com.pontuo.api_pontuo.controller.MockExamController;
import com.pontuo.api_pontuo.controller.MockExamHasQuestionController;
import com.pontuo.api_pontuo.controller.QuestionController;
import com.pontuo.api_pontuo.controller.StateController;
import com.pontuo.api_pontuo.controller.UserController;
import com.pontuo.api_pontuo.controller.UserRoleController;
import com.pontuo.api_pontuo.entity.MockExam;
import com.pontuo.api_pontuo.entity.MockExamHasQuestion;
import com.pontuo.api_pontuo.entity.State;
import com.pontuo.api_pontuo.security.JwtService;
import com.pontuo.api_pontuo.service.MockExamHasQuestionService;
import com.pontuo.api_pontuo.service.MockExamService;
import com.pontuo.api_pontuo.service.QuestionService;
import com.pontuo.api_pontuo.service.StateService;
import com.pontuo.api_pontuo.service.UserRoleService;
import com.pontuo.api_pontuo.service.UserService;
import com.pontuo.api_pontuo.support.WithApiSecurity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = {
        AuthController.class,
        StateController.class,
        QuestionController.class,
        MockExamController.class,
        MockExamHasQuestionController.class,
        UserController.class,
        UserRoleController.class
})
@WithApiSecurity
@DisplayName("Proteção das rotas")
class RouteAuthorizationTest {

    private static final String SIMULADO_JSON = """
            {"name": "Simulado ENEM", "numQuestions": 10, "maxTime": 90, "correctCount": 0, "userId": 7}
            """;
    private static final String QUESTAO_JSON = """
            {"statement": "Enunciado", "difficulty": 2, "entranceExamId": 1, "topicId": 2}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserRoleService userRoleService;

    @MockitoBean
    private StateService stateService;

    @MockitoBean
    private QuestionService questionService;

    @MockitoBean
    private MockExamService mockExamService;

    @MockitoBean
    private MockExamHasQuestionService mockExamHasQuestionService;

    private static JwtRequestPostProcessor estudante() {
        return jwt().jwt(token -> token.subject("maria"))
                .authorities(new SimpleGrantedAuthority("ROLE_ESTUDANTE"));
    }

    private static JwtRequestPostProcessor administrador() {
        return jwt().jwt(token -> token.subject("admin"))
                .authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"));
    }

    @Nested
    @DisplayName("sem token")
    class SemToken {

        @Test
        @DisplayName("catálogo responde 401 em JSON pedindo Bearer")
        void deveExigirTokenNoCatalogo() throws Exception {
            mockMvc.perform(get("/api/states"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, "Bearer"))
                    .andExpect(jsonPath("$.message")
                            .value("Autenticação necessária: envie um token válido no header Authorization."));
        }

        @Test
        @DisplayName("/api/auth/me e /api/auth/logout exigem token")
        void deveExigirTokenNasRotasDaConta() throws Exception {
            mockMvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
            mockMvc.perform(post("/api/auth/logout")).andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("estudante")
    class Estudante {

        @Test
        @DisplayName("lê o catálogo")
        void deveLerCatalogo() throws Exception {
            mockMvc.perform(get("/api/questions").with(estudante()))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("cria, altera e remove simulados")
        void deveGerenciarSimulados() throws Exception {
            when(mockExamService.create(any(MockExam.class), anyLong()))
                    .thenReturn(new MockExam("Simulado ENEM", (short) 10, (short) 90));
            when(mockExamHasQuestionService.update(anyLong(), anyLong(), anyLong(), isNull(), isNull()))
                    .thenReturn(new MockExamHasQuestion());

            mockMvc.perform(post("/api/mock-exams").with(estudante())
                            .contentType(MediaType.APPLICATION_JSON).content(SIMULADO_JSON))
                    .andExpect(status().isCreated());
            mockMvc.perform(put("/api/mock-exam-questions/1").with(estudante())
                            .contentType(MediaType.APPLICATION_JSON).content("{\"mockExamId\": 1, \"questionId\": 2}"))
                    .andExpect(status().isOk());
            mockMvc.perform(delete("/api/mock-exams/1").with(estudante()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("não altera o catálogo: 403 em JSON")
        void naoDeveAlterarCatalogo() throws Exception {
            mockMvc.perform(post("/api/questions").with(estudante())
                            .contentType(MediaType.APPLICATION_JSON).content(QUESTAO_JSON))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message")
                            .value("Acesso negado: seu perfil não tem permissão para este recurso."));
            mockMvc.perform(delete("/api/states/1").with(estudante()))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("não acessa a gestão de usuários e perfis, nem para leitura")
        void naoDeveAcessarGestaoDeUsuarios() throws Exception {
            mockMvc.perform(get("/api/users").with(estudante())).andExpect(status().isForbidden());
            mockMvc.perform(get("/api/user-roles").with(estudante())).andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("administrador")
    class Administrador {

        @Test
        @DisplayName("altera o catálogo")
        void deveAlterarCatalogo() throws Exception {
            when(stateService.create(any(State.class))).thenReturn(new State("Acre"));

            mockMvc.perform(post("/api/states").with(administrador())
                            .contentType(MediaType.APPLICATION_JSON).content("{\"name\": \"Acre\"}"))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("gerencia usuários e perfis")
        void deveGerenciarUsuarios() throws Exception {
            mockMvc.perform(get("/api/users").with(administrador())).andExpect(status().isOk());
            mockMvc.perform(delete("/api/user-roles/3").with(administrador())).andExpect(status().isNoContent());
        }
    }
}
