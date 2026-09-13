package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.MockExam;
import com.pontuo.api_pontuo.entity.MockExamHasQuestion;
import com.pontuo.api_pontuo.entity.Question;
import com.pontuo.api_pontuo.entity.User;
import com.pontuo.api_pontuo.support.RequiresDatabase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(DatabaseFixtures.class)
@RequiresDatabase
@DisplayName("MockExamHasQuestionRepository (MariaDB)")
class MockExamHasQuestionRepositoryTest {

    @Autowired
    private MockExamHasQuestionRepository repository;

    @Autowired
    private DatabaseFixtures fixtures;

    @Test
    @DisplayName("existsByMockExamIdAndQuestionId considera o par simulado + questão")
    void deveDetectarParJaVinculado() {
        User aluno = fixtures.estudante("junit_aluno_vinculo");
        MockExam simulado = fixtures.simulado("Simulado de teste", 10, aluno);
        MockExam outroSimulado = fixtures.simulado("Outro simulado de teste", 10, aluno);
        Question questao = fixtures.questao("Enunciado de teste");
        Question outraQuestao = fixtures.questao("Outro enunciado de teste");

        repository.save(new MockExamHasQuestion(simulado, questao));

        assertAll(
                () -> assertTrue(repository.existsByMockExamIdAndQuestionId(simulado.getId(), questao.getId())),
                () -> assertFalse(repository.existsByMockExamIdAndQuestionId(simulado.getId(), outraQuestao.getId())),
                () -> assertFalse(repository.existsByMockExamIdAndQuestionId(outroSimulado.getId(), questao.getId())));
    }
}
