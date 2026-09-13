package com.pontuo.api_pontuo.integration;

import com.pontuo.api_pontuo.entity.MockExam;
import com.pontuo.api_pontuo.entity.Question;
import com.pontuo.api_pontuo.entity.User;
import com.pontuo.api_pontuo.repository.DatabaseFixtures;
import com.pontuo.api_pontuo.repository.MockExamRepository;
import com.pontuo.api_pontuo.support.Pendente;
import com.pontuo.api_pontuo.support.RequiresDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(DatabaseFixtures.class)
@RequiresDatabase
@DisplayName("Simulados: regras pendentes do README")
class MockExamPendingRulesTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DatabaseFixtures fixtures;

    @Autowired
    private MockExamRepository mockExamRepository;

    private User alunoA;
    private User alunoB;
    private MockExam simuladoDeA;
    private MockExam simuladoDeB;

    @BeforeEach
    void criarCenario() {
        alunoA = fixtures.estudante("junit_aluno_a");
        alunoB = fixtures.estudante("junit_aluno_b");
        simuladoDeA = fixtures.simulado("Simulado de A", 10, alunoA);
        simuladoDeB = fixtures.simulado("Simulado de B", 10, alunoB);
    }

    private static JwtRequestPostProcessor comoAluno(User aluno) {
        return jwt().jwt(token -> token.subject(aluno.getUsername()))
                .authorities(new SimpleGrantedAuthority("ROLE_ESTUDANTE"));
    }

    private static String simuladoJson(String nome, User dono) {
        return """
                {"name": "%s", "numQuestions": 10, "maxTime": 90, "correctCount": 0, "userId": %d}
                """.formatted(nome, dono.getId());
    }

    @Nested
    @DisplayName("propriedade do simulado")
    class Propriedade {

        @Test
        @DisplayName("o dono lê o próprio simulado")
        void donoDeveLerOProprioSimulado() throws Exception {
            mockMvc.perform(get("/api/mock-exams/{id}", simuladoDeA.getId()).with(comoAluno(alunoA)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Simulado de A"));
        }

        @Test
        @DisplayName("o dono cria simulado para si mesmo")
        void donoDeveCriarSimuladoParaSi() throws Exception {
            mockMvc.perform(post("/api/mock-exams").with(comoAluno(alunoA))
                            .contentType(MediaType.APPLICATION_JSON).content(simuladoJson("Novo simulado", alunoA)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.user.username").value("junit_aluno_a"));
        }

        @Test
        @Pendente("filtrar GET /api/mock-exams pelo usuário do token")
        @DisplayName("a listagem do estudante traz só os simulados dele")
        void deveListarSoOsPropriosSimulados() throws Exception {
            mockMvc.perform(get("/api/mock-exams").with(comoAluno(alunoA)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].user.username").value("junit_aluno_a"));
        }

        @Test
        @Pendente("verificar o dono em GET /api/mock-exams/{id}")
        @DisplayName("estudante não lê o simulado de outro")
        void naoDeveLerSimuladoDeOutro() throws Exception {
            mockMvc.perform(get("/api/mock-exams/{id}", simuladoDeB.getId()).with(comoAluno(alunoA)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @Pendente("verificar o dono em PUT /api/mock-exams/{id}")
        @DisplayName("estudante não altera o simulado de outro")
        void naoDeveAlterarSimuladoDeOutro() throws Exception {
            mockMvc.perform(put("/api/mock-exams/{id}", simuladoDeB.getId()).with(comoAluno(alunoA))
                            .contentType(MediaType.APPLICATION_JSON).content(simuladoJson("Alterado por A", alunoA)))
                    .andExpect(status().isForbidden());

            assertEquals("Simulado de B", mockExamRepository.findById(simuladoDeB.getId()).orElseThrow().getName());
        }

        @Test
        @Pendente("verificar o dono em DELETE /api/mock-exams/{id}")
        @DisplayName("estudante não remove o simulado de outro")
        void naoDeveRemoverSimuladoDeOutro() throws Exception {
            mockMvc.perform(delete("/api/mock-exams/{id}", simuladoDeB.getId()).with(comoAluno(alunoA)))
                    .andExpect(status().isForbidden());

            assertTrue(mockExamRepository.existsById(simuladoDeB.getId()));
        }

        @Test
        @Pendente("impedir que o estudante informe o userId de outra pessoa")
        @DisplayName("estudante não cria simulado em nome de outro")
        void naoDeveCriarSimuladoEmNomeDeOutro() throws Exception {
            mockMvc.perform(post("/api/mock-exams").with(comoAluno(alunoA))
                            .contentType(MediaType.APPLICATION_JSON).content(simuladoJson("Simulado falso", alunoB)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("limite de questões")
    class LimiteDeQuestoes {

        private String vinculoJson(MockExam simulado, Question questao) {
            return "{\"mockExamId\": %d, \"questionId\": %d}".formatted(simulado.getId(), questao.getId());
        }

        @Test
        @Pendente("limitar os vínculos de mock_exam_has_question a numQuestions (se a regra for desejada)")
        @DisplayName("recusa vincular mais questões do que numQuestions")
        void deveRecusarQuestaoAlemDoLimite() throws Exception {
            MockExam umaQuestao = fixtures.simulado("Simulado de uma questão", 1, alunoA);
            Question primeira = fixtures.questao("Primeira questão de teste");
            Question segunda = fixtures.questao("Segunda questão de teste");

            mockMvc.perform(post("/api/mock-exam-questions").with(comoAluno(alunoA))
                            .contentType(MediaType.APPLICATION_JSON).content(vinculoJson(umaQuestao, primeira)))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/api/mock-exam-questions").with(comoAluno(alunoA))
                            .contentType(MediaType.APPLICATION_JSON).content(vinculoJson(umaQuestao, segunda)))
                    .andExpect(status().is4xxClientError());
        }
    }
}
