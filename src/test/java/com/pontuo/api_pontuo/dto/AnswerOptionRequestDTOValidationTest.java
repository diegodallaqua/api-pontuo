package com.pontuo.api_pontuo.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("AnswerOptionRequestDTO")
class AnswerOptionRequestDTOValidationTest extends AbstractDtoValidationTest {

    @ParameterizedTest(name = "letra \"{0}\" é aceita")
    @ValueSource(strings = {"A", "C", "E"})
    void deveAceitarLetrasDeAaE(String letra) {
        assertTrue(violacoes(new AnswerOptionRequestDTO(letra, "Texto", false, 10L)).isEmpty());
    }

    @ParameterizedTest(name = "letra \"{0}\" é rejeitada")
    @ValueSource(strings = {"F", "a", "AB"})
    void deveRejeitarLetraForaDoPadrao(String letra) {
        AnswerOptionRequestDTO dto = new AnswerOptionRequestDTO(letra, "Texto", false, 10L);

        assertEquals(Set.of("letter deve ser de A a E"), mensagens(dto, "letter"));
    }

    @Test
    @DisplayName("exige o texto da alternativa")
    void deveExigirTexto() {
        AnswerOptionRequestDTO dto = new AnswerOptionRequestDTO("A", " ", false, 10L);

        assertEquals(Set.of("text é obrigatório"), mensagens(dto, "text"));
    }

    @Test
    @DisplayName("exige a questão")
    void deveExigirQuestao() {
        AnswerOptionRequestDTO dto = new AnswerOptionRequestDTO("A", "Texto", false, null);

        assertEquals(Set.of("questionId é obrigatório"), mensagens(dto, "questionId"));
    }
}
