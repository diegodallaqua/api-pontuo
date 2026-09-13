package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.AnswerOption;
import com.pontuo.api_pontuo.entity.Question;
import com.pontuo.api_pontuo.support.RequiresDatabase;
import org.junit.jupiter.api.BeforeEach;
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
@DisplayName("AnswerOptionRepository (MariaDB)")
class AnswerOptionRepositoryTest {

    @Autowired
    private AnswerOptionRepository repository;

    @Autowired
    private DatabaseFixtures fixtures;

    private Question questao;
    private Question outraQuestao;

    @BeforeEach
    void criarQuestoes() {
        questao = fixtures.questao("Enunciado de teste");
        outraQuestao = fixtures.questao("Outro enunciado de teste");
    }

    @Test
    @DisplayName("existsByQuestionIdAndLetter só enxerga a letra dentro da própria questão")
    void deveEncontrarLetraSoNaPropriaQuestao() {
        repository.save(new AnswerOption("A", "Texto", false, questao));

        assertAll(
                () -> assertTrue(repository.existsByQuestionIdAndLetter(questao.getId(), "A")),
                () -> assertFalse(repository.existsByQuestionIdAndLetter(questao.getId(), "B")),
                () -> assertFalse(repository.existsByQuestionIdAndLetter(outraQuestao.getId(), "A")));
    }

    @Test
    @DisplayName("existsByQuestionIdAndRightAnswerTrue ignora alternativas incorretas")
    void deveDetectarSomenteAlternativaCorreta() {
        repository.save(new AnswerOption("A", "Texto", false, questao));
        assertFalse(repository.existsByQuestionIdAndRightAnswerTrue(questao.getId()));

        repository.save(new AnswerOption("B", "Texto", true, questao));
        assertAll(
                () -> assertTrue(repository.existsByQuestionIdAndRightAnswerTrue(questao.getId())),
                () -> assertFalse(repository.existsByQuestionIdAndRightAnswerTrue(outraQuestao.getId())));
    }
}
