package com.pontuo.api_pontuo.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("MockExamRequestDTO")
class MockExamRequestDTOValidationTest extends AbstractDtoValidationTest {

    private static MockExamRequestDTO simulado(int numQuestions, int maxTime) {
        return new MockExamRequestDTO("Simulado ENEM", (short) numQuestions, (short) maxTime, 7L);
    }

    @Test
    @DisplayName("simulado completo passa na validação")
    void deveAceitarSimuladoValido() {
        assertTrue(violacoes(simulado(10, 90)).isEmpty());
    }

    @Test
    @DisplayName("rejeita simulado sem questões")
    void deveRejeitarZeroQuestoes() {
        assertEquals(Set.of("numQuestions deve ser maior que zero"), mensagens(simulado(0, 90), "numQuestions"));
    }

    @Test
    @DisplayName("rejeita tempo máximo zero")
    void deveRejeitarTempoZero() {
        assertEquals(Set.of("maxTime deve ser maior que zero"), mensagens(simulado(10, 0), "maxTime"));
    }

    @Test
    @DisplayName("exige o nome")
    void deveExigirNome() {
        MockExamRequestDTO dto = new MockExamRequestDTO(" ", (short) 10, (short) 90, null);

        assertEquals(Set.of("name é obrigatório"), mensagens(dto, "name"));
    }

    @Test
    @DisplayName("userId é opcional: sem ele, o dono é o usuário do token")
    void deveAceitarSemUsuario() {
        MockExamRequestDTO dto = new MockExamRequestDTO("Simulado ENEM", (short) 10, (short) 90, null);

        assertTrue(violacoes(dto).isEmpty());
    }
}
