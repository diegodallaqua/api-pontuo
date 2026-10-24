package com.pontuo.api_pontuo.integration;

import com.jayway.jsonpath.JsonPath;
import com.pontuo.api_pontuo.entity.AnswerOption;
import com.pontuo.api_pontuo.entity.MockExam;
import com.pontuo.api_pontuo.entity.Question;
import com.pontuo.api_pontuo.entity.User;
import com.pontuo.api_pontuo.repository.DatabaseFixtures;
import com.pontuo.api_pontuo.repository.MockExamHasQuestionRepository;
import com.pontuo.api_pontuo.repository.MockExamRepository;
import com.pontuo.api_pontuo.repository.QuestionRepository;
import com.pontuo.api_pontuo.repository.SubjectRepository;
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

import java.time.LocalDateTime;

import static com.pontuo.api_pontuo.repository.DatabaseFixtures.BIOLOGIA_ID;
import static com.pontuo.api_pontuo.repository.DatabaseFixtures.GENETICA_ID;
import static com.pontuo.api_pontuo.repository.DatabaseFixtures.QUIMICA_ID;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Fluxo completo contra o MariaDB: montar o simulado, iniciar, responder,
 * finalizar e consultar o desempenho. O simulado tem 4 questões:
 *
 * <pre>
 * Genética (tópico de Biologia), dificuldade 1 — acerta
 * Biologia (matéria),            dificuldade 2 — erra
 * Química (matéria),             dificuldade 3 — acerta
 * Química (matéria),             dificuldade 3 — em branco
 * </pre>
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(DatabaseFixtures.class)
@RequiresDatabase
@DisplayName("Desempenho: fluxo do simulado até a análise")
class PerformanceFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DatabaseFixtures fixtures;

    @Autowired
    private MockExamHasQuestionRepository linkRepository;

    @Autowired
    private MockExamRepository mockExamRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    private User aluno;
    private MockExam simulado;
    private Question genetica;
    private Question biologia;
    private Question quimica;
    private Question quimicaEmBranco;
    private AnswerOption[] alternativasGenetica;
    private AnswerOption[] alternativasBiologia;
    private AnswerOption[] alternativasQuimica;

    @BeforeEach
    void criarCenario() {
        aluno = fixtures.estudante("junit_desempenho");
        simulado = fixtures.simulado("Simulado de desempenho", 4, aluno);
        genetica = fixtures.questaoDoTopico("Questão de genética", GENETICA_ID, (short) 1);
        biologia = fixtures.questaoDaMateria("Questão de biologia", BIOLOGIA_ID, (short) 2);
        quimica = fixtures.questaoDaMateria("Questão de química", QUIMICA_ID, (short) 3);
        quimicaEmBranco = fixtures.questaoDaMateria("Questão de química em branco", QUIMICA_ID, (short) 3);
        alternativasGenetica = fixtures.alternativas(genetica);
        alternativasBiologia = fixtures.alternativas(biologia);
        alternativasQuimica = fixtures.alternativas(quimica);
        fixtures.alternativas(quimicaEmBranco);
    }

    private static JwtRequestPostProcessor como(User usuario) {
        return jwt().jwt(token -> token.subject(usuario.getUsername()))
                .authorities(new SimpleGrantedAuthority("ROLE_ESTUDANTE"));
    }

    private static String vinculoJson(MockExam simulado, Question questao, AnswerOption marcada) {
        return "{\"mockExamId\": %d, \"questionId\": %d, \"answerOptionId\": %s}".formatted(
                simulado.getId(), questao.getId(), marcada == null ? "null" : marcada.getId());
    }

    private long vincular(Question questao, AnswerOption marcada) throws Exception {
        String corpo = mockMvc.perform(post("/api/mock-exam-questions").with(como(aluno))
                        .contentType(MediaType.APPLICATION_JSON).content(vinculoJson(simulado, questao, marcada)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(corpo, "$.id")).longValue();
    }

    private long resolverEFinalizar() throws Exception {
        mockMvc.perform(post("/api/mock-exams/{id}/start", simulado.getId()).with(como(aluno)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startedAt").isNotEmpty());
        long vinculoGenetica = vincular(genetica, alternativasGenetica[0]);
        vincular(biologia, alternativasBiologia[1]);
        vincular(quimica, alternativasQuimica[0]);
        vincular(quimicaEmBranco, null);
        mockMvc.perform(post("/api/mock-exams/{id}/finish", simulado.getId()).with(como(aluno)))
                .andExpect(status().isOk());
        return vinculoGenetica;
    }

    @Nested
    @DisplayName("simulado")
    class Simulado {

        @Test
        @DisplayName("prazo vencido bloqueia vínculos, mas permite finalizar")
        void deveAplicarPrazoNoServidor() throws Exception {
            simulado.setStartedAt(LocalDateTime.now().minusMinutes(91));
            mockExamRepository.saveAndFlush(simulado);

            mockMvc.perform(post("/api/mock-exam-questions").with(como(aluno))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(vinculoJson(simulado, genetica, null)))
                    .andExpect(status().isConflict());

            mockMvc.perform(put("/api/mock-exams/{id}", simulado.getId()).with(como(aluno))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Alterado\",\"numQuestions\":4,\"maxTime\":90}"))
                    .andExpect(status().isConflict());

            mockMvc.perform(post("/api/mock-exams/{id}/finish", simulado.getId()).with(como(aluno)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.mockExam.finishedAt").isNotEmpty());
        }

        @Test
        @DisplayName("não aceita resposta antes do início")
        void deveRecusarRespostaAntesDoInicio() throws Exception {
            mockMvc.perform(post("/api/mock-exam-questions").with(como(aluno))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(vinculoJson(simulado, genetica, alternativasGenetica[0])))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("a correção só aparece após finalizar")
        void deveOcultarCorrecaoAteFinalizar() throws Exception {
            mockMvc.perform(post("/api/mock-exams/{id}/start", simulado.getId()).with(como(aluno)))
                    .andExpect(status().isOk());

            String resposta = mockMvc.perform(post("/api/mock-exam-questions").with(como(aluno))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(vinculoJson(simulado, biologia, alternativasBiologia[1])))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.correct").doesNotExist())
                    .andExpect(jsonPath("$.answeredAt").isNotEmpty())
                    .andExpect(jsonPath("$.answerOption.rightAnswer").doesNotExist())
                    .andReturn().getResponse().getContentAsString();

            mockMvc.perform(post("/api/mock-exams/{id}/finish", simulado.getId()).with(como(aluno)))
                    .andExpect(status().isOk());
            long linkId = ((Number) JsonPath.read(resposta, "$.id")).longValue();
            mockMvc.perform(get("/api/mock-exam-questions/{id}", linkId).with(como(aluno)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.correct").value(false));
        }

        @Test
        @DisplayName("finalizar calcula acertos / numQuestions e o boletim por matéria")
        void deveFinalizarComBoletim() throws Exception {
            mockMvc.perform(post("/api/mock-exams/{id}/start", simulado.getId()).with(como(aluno)))
                    .andExpect(status().isOk());
            vincular(genetica, alternativasGenetica[0]);
            vincular(biologia, alternativasBiologia[1]);
            vincular(quimica, alternativasQuimica[0]);
            vincular(quimicaEmBranco, null);

            mockMvc.perform(post("/api/mock-exams/{id}/finish", simulado.getId()).with(como(aluno)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.mockExam.finishedAt").isNotEmpty())
                    .andExpect(jsonPath("$.mockExam.answeredCount").value(3))
                    .andExpect(jsonPath("$.mockExam.correctCount").value(2))
                    .andExpect(jsonPath("$.mockExam.accuracy").value(50.0))
                    .andExpect(jsonPath("$.subjects", hasSize(2)))
                    .andExpect(jsonPath("$.subjects[0].description").value("Biologia"))
                    .andExpect(jsonPath("$.subjects[0].questions").value(2))
                    .andExpect(jsonPath("$.subjects[0].correct").value(1))
                    .andExpect(jsonPath("$.subjects[1].description").value("Química"))
                    .andExpect(jsonPath("$.subjects[1].answered").value(1))
                    .andExpect(jsonPath("$.subjects[1].accuracy").value(50.0));

            mockMvc.perform(get("/api/mock-exams/{id}/result", simulado.getId()).with(como(aluno)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.subjects", hasSize(2)));
        }

        @Test
        @DisplayName("depois de finalizado, simulado e respostas ficam travados")
        void deveTravarDepoisDeFinalizado() throws Exception {
            long vinculoGenetica = resolverEFinalizar();

            mockMvc.perform(put("/api/mock-exam-questions/{id}", vinculoGenetica).with(como(aluno))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(vinculoJson(simulado, genetica, alternativasGenetica[1])))
                    .andExpect(status().isConflict());
            mockMvc.perform(put("/api/mock-exams/{id}", simulado.getId()).with(como(aluno))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\": \"Renomeado\", \"numQuestions\": 4, \"maxTime\": 90}"))
                    .andExpect(status().isConflict());
            mockMvc.perform(post("/api/mock-exams/{id}/finish", simulado.getId()).with(como(aluno)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("simulado finalizado pode ser excluído, levando os vínculos junto")
        void deveExcluirSimuladoFinalizado() throws Exception {
            resolverEFinalizar();

            mockMvc.perform(delete("/api/mock-exams/{id}", simulado.getId()).with(como(aluno)))
                    .andExpect(status().isNoContent());

            assertEquals(0, linkRepository.countByMockExamId(simulado.getId()));
            mockMvc.perform(get("/api/mock-exams/{id}/result", simulado.getId()).with(como(aluno)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("análise de desempenho")
    class Analise {

        @Test
        @DisplayName("resumo soma os simulados finalizados")
        void deveResumir() throws Exception {
            resolverEFinalizar();

            mockMvc.perform(get("/api/performance/me/overview").with(como(aluno)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.finishedMockExams").value(1))
                    .andExpect(jsonPath("$.questions").value(4))
                    .andExpect(jsonPath("$.answered").value(3))
                    .andExpect(jsonPath("$.correct").value(2))
                    .andExpect(jsonPath("$.accuracy").value(50.0));
        }

        @Test
        @DisplayName("simulado não finalizado não entra na análise")
        void deveIgnorarSimuladoAberto() throws Exception {
            mockMvc.perform(post("/api/mock-exams/{id}/start", simulado.getId()).with(como(aluno)))
                    .andExpect(status().isOk());
            vincular(genetica, alternativasGenetica[0]);

            mockMvc.perform(get("/api/performance/me/overview").with(como(aluno)))
                    .andExpect(jsonPath("$.finishedMockExams").value(0));
            mockMvc.perform(get("/api/performance/me/subjects").with(como(aluno)))
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("por área e matéria, a questão de tópico conta na matéria do tópico")
        void deveAgruparPorAreaEMateria() throws Exception {
            resolverEFinalizar();

            mockMvc.perform(get("/api/performance/me/knowledge-areas").with(como(aluno)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].description").value("Ciências da Natureza"))
                    .andExpect(jsonPath("$[0].questions").value(4))
                    .andExpect(jsonPath("$[0].answered").value(3))
                    .andExpect(jsonPath("$[0].correct").value(2));

            mockMvc.perform(get("/api/performance/me/subjects").with(como(aluno)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].description").value("Biologia"))
                    .andExpect(jsonPath("$[0].questions").value(2))
                    .andExpect(jsonPath("$[0].correct").value(1))
                    .andExpect(jsonPath("$[0].accuracy").value(50.0))
                    .andExpect(jsonPath("$[1].description").value("Química"));
        }

        @Test
        @DisplayName("por tópico só entram questões classificadas por tópico")
        void deveAgruparPorTopico() throws Exception {
            resolverEFinalizar();

            mockMvc.perform(get("/api/performance/me/topics").param("subjectId", String.valueOf(BIOLOGIA_ID))
                            .with(como(aluno)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].description").value("Genética"))
                    .andExpect(jsonPath("$[0].accuracy").value(100.0));
        }

        @Test
        @DisplayName("por dificuldade")
        void deveAgruparPorDificuldade() throws Exception {
            resolverEFinalizar();

            mockMvc.perform(get("/api/performance/me/difficulty").with(como(aluno)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[2].id").value(3))
                    .andExpect(jsonPath("$[2].questions").value(2))
                    .andExpect(jsonPath("$[2].correct").value(1));
        }

        @Test
        @DisplayName("evolução por matéria usa o boletim de cada simulado")
        void deveMostrarEvolucaoPorMateria() throws Exception {
            resolverEFinalizar();

            mockMvc.perform(get("/api/performance/me/evolution").param("subjectId", String.valueOf(QUIMICA_ID))
                            .with(como(aluno)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].mockExamName").value("Simulado de desempenho"))
                    .andExpect(jsonPath("$[0].questions").value(2))
                    .andExpect(jsonPath("$[0].accuracy").value(50.0));
        }

        @Test
        @DisplayName("filtro por vestibular e por período")
        void deveFiltrar() throws Exception {
            resolverEFinalizar();

            mockMvc.perform(get("/api/performance/me/subjects").param("entranceExamId", "2").with(como(aluno)))
                    .andExpect(jsonPath("$", hasSize(0)));
            mockMvc.perform(get("/api/performance/me/overview").param("to", "2000-01-01").with(como(aluno)))
                    .andExpect(jsonPath("$.finishedMockExams").value(0));
        }

        @Test
        @DisplayName("cada estudante vê só o próprio desempenho")
        void deveIsolarEstudantes() throws Exception {
            resolverEFinalizar();
            User outro = fixtures.estudante("junit_desempenho_outro");

            mockMvc.perform(get("/api/performance/me/overview").with(como(outro)))
                    .andExpect(jsonPath("$.finishedMockExams").value(0));
            mockMvc.perform(get("/api/mock-exams/{id}/result", simulado.getId()).with(como(outro)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("reclassificar a questão reflete igual no boletim, na matéria e na evolução")
        void deveUsarUmaUnicaRegraDeMateria() throws Exception {
            resolverEFinalizar();
            // A questão de Biologia (errada) passa a ser de Química depois da prova.
            biologia.setSubject(subjectRepository.findById(QUIMICA_ID).orElseThrow());
            questionRepository.save(biologia);

            mockMvc.perform(get("/api/mock-exams/{id}/result", simulado.getId()).with(como(aluno)))
                    .andExpect(jsonPath("$.subjects[0].description").value("Biologia"))
                    .andExpect(jsonPath("$.subjects[0].questions").value(1))
                    .andExpect(jsonPath("$.subjects[1].description").value("Química"))
                    .andExpect(jsonPath("$.subjects[1].questions").value(3))
                    .andExpect(jsonPath("$.subjects[1].correct").value(1));
            mockMvc.perform(get("/api/performance/me/subjects").with(como(aluno)))
                    .andExpect(jsonPath("$[0].questions").value(1))
                    .andExpect(jsonPath("$[1].questions").value(3));
            mockMvc.perform(get("/api/performance/me/evolution").param("subjectId", String.valueOf(QUIMICA_ID))
                            .with(como(aluno)))
                    .andExpect(jsonPath("$[0].questions").value(3))
                    .andExpect(jsonPath("$[0].accuracy").value(33.33));
        }
    }
}
