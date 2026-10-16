package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.AnswerOption;
import com.pontuo.api_pontuo.entity.MockExam;
import com.pontuo.api_pontuo.entity.MockExamHasQuestion;
import com.pontuo.api_pontuo.entity.Question;
import com.pontuo.api_pontuo.entity.Subject;
import com.pontuo.api_pontuo.entity.Topic;
import com.pontuo.api_pontuo.entity.User;
import com.pontuo.api_pontuo.support.RequiresDatabase;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;

import static com.pontuo.api_pontuo.repository.DatabaseFixtures.BIOLOGIA_ID;
import static com.pontuo.api_pontuo.repository.DatabaseFixtures.GENETICA_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Restrições que o banco garante sozinho, independentemente dos services
 * (migration V9). Os registros são gravados direto pelos repositórios, sem
 * passar pelas validações da aplicação.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(DatabaseFixtures.class)
@RequiresDatabase
@DisplayName("Restrições do schema (MariaDB)")
class SchemaConstraintsTest {

    @Autowired
    private DatabaseFixtures fixtures;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private AnswerOptionRepository answerOptionRepository;

    @Autowired
    private MockExamHasQuestionRepository linkRepository;

    @Autowired
    private EntityManager entityManager;

    @Nested
    @DisplayName("questão: tópico OU matéria")
    class TopicoOuMateria {

        private Question questao(Topic topico, Subject materia) {
            Question referencia = fixtures.questao("Questão de referência");
            return new Question("Questão de teste", null, (short) 2, referencia.getEntranceExam(), topico, materia);
        }

        @Test
        @DisplayName("recusa questão com tópico e matéria ao mesmo tempo")
        void deveRecusarOsDois() {
            Question questao = questao(topicRepository.findById(GENETICA_ID).orElseThrow(),
                    subjectRepository.findById(BIOLOGIA_ID).orElseThrow());

            assertThrows(DataIntegrityViolationException.class, () -> questionRepository.saveAndFlush(questao));
        }

        @Test
        @DisplayName("recusa questão sem tópico nem matéria")
        void deveRecusarNenhum() {
            Question questao = questao(null, null);

            assertThrows(DataIntegrityViolationException.class, () -> questionRepository.saveAndFlush(questao));
        }

        @Test
        @DisplayName("aceita questão só com tópico")
        void deveAceitarSoTopico() {
            Question questao = questao(topicRepository.findById(GENETICA_ID).orElseThrow(), null);

            assertNotNull(questionRepository.saveAndFlush(questao).getId());
        }
    }

    @Nested
    @DisplayName("alternativa marcada pertence à questão do vínculo")
    class AlternativaDaQuestao {

        private MockExam simulado;
        private Question questao;
        private Question outraQuestao;
        private AnswerOption alternativaDaOutra;

        @BeforeEach
        void criarCenario() {
            User aluno = fixtures.estudante("junit_schema");
            simulado = fixtures.simulado("Simulado de schema", 5, aluno);
            questao = fixtures.questao("Questão do vínculo");
            outraQuestao = fixtures.questao("Outra questão");
            fixtures.alternativas(questao);
            alternativaDaOutra = fixtures.alternativas(outraQuestao)[0];
        }

        @Test
        @DisplayName("recusa vínculo com alternativa de outra questão")
        void deveRecusarAlternativaDeOutraQuestao() {
            MockExamHasQuestion vinculo = new MockExamHasQuestion(
                    simulado, questao, alternativaDaOutra, LocalDateTime.now());

            assertThrows(DataIntegrityViolationException.class, () -> linkRepository.saveAndFlush(vinculo));
        }

        @Test
        @DisplayName("aceita vínculo sem resposta")
        void deveAceitarSemResposta() {
            assertNotNull(linkRepository.saveAndFlush(new MockExamHasQuestion(simulado, questao)).getId());
        }

        @Test
        @DisplayName("recusa mover para outra questão uma alternativa já respondida")
        void deveRecusarMoverAlternativaRespondida() {
            Question terceira = fixtures.questao("Terceira questão");
            linkRepository.saveAndFlush(new MockExamHasQuestion(
                    simulado, outraQuestao, alternativaDaOutra, LocalDateTime.now()));
            alternativaDaOutra.setQuestion(terceira);

            assertThrows(DataIntegrityViolationException.class,
                    () -> answerOptionRepository.saveAndFlush(alternativaDaOutra));
        }
    }

    @Test
    @DisplayName("índices redundantes e mock_exam_subject_result não existem mais")
    void deveTerRemovidoRedundancias() {
        Number indices = (Number) entityManager.createNativeQuery("""
                SELECT COUNT(*) FROM information_schema.statistics
                WHERE table_schema = DATABASE()
                  AND index_name IN ('idx_answer_option_question', 'idx_mock_exam_user')
                """).getSingleResult();
        Number tabelas = (Number) entityManager.createNativeQuery("""
                SELECT COUNT(*) FROM information_schema.tables
                WHERE table_schema = DATABASE() AND table_name = 'mock_exam_subject_result'
                """).getSingleResult();

        assertEquals(0, indices.intValue());
        assertEquals(0, tabelas.intValue());
    }
}
