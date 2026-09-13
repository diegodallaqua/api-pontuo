package com.pontuo.api_pontuo.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("QuestionRequestDTO")
class QuestionRequestDTOValidationTest extends AbstractDtoValidationTest {

    private static QuestionRequestDTO comDificuldade(Short dificuldade) {
        return new QuestionRequestDTO("Enunciado", null, dificuldade, 1L, 2L, null);
    }

    @ParameterizedTest(name = "dificuldade {0} é aceita")
    @ValueSource(shorts = {1, 3})
    void deveAceitarDificuldadeNosLimites(short dificuldade) {
        assertTrue(violacoes(comDificuldade(dificuldade)).isEmpty());
    }

    @ParameterizedTest(name = "dificuldade {0} é rejeitada")
    @ValueSource(shorts = {0, 4})
    void deveRejeitarDificuldadeForaDosLimites(short dificuldade) {
        assertEquals(Set.of("difficulty deve estar entre 1 e 3"), mensagens(comDificuldade(dificuldade), "difficulty"));
    }

    @Test
    @DisplayName("dificuldade é opcional")
    void deveAceitarDificuldadeNaoInformada() {
        assertTrue(violacoes(comDificuldade(null)).isEmpty());
    }

    @Test
    @DisplayName("exige o enunciado")
    void deveExigirEnunciado() {
        QuestionRequestDTO dto = new QuestionRequestDTO("  ", null, (short) 2, 1L, 2L, null);

        assertEquals(Set.of("statement é obrigatório"), mensagens(dto, "statement"));
    }

    @Test
    @DisplayName("exige o vestibular")
    void deveExigirVestibular() {
        QuestionRequestDTO dto = new QuestionRequestDTO("Enunciado", null, (short) 2, null, 2L, null);

        assertEquals(Set.of("entranceExamId é obrigatório"), mensagens(dto, "entranceExamId"));
    }
}
