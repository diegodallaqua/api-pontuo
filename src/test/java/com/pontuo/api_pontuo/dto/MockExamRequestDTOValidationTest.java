package com.pontuo.api_pontuo.dto;

import com.pontuo.api_pontuo.support.Pendente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("MockExamRequestDTO")
class MockExamRequestDTOValidationTest extends AbstractDtoValidationTest {

    private static MockExamRequestDTO simulado(int numQuestions, int maxTime, int correctCount, String accuracy) {
        return new MockExamRequestDTO("Simulado ENEM", (short) numQuestions, (short) maxTime,
                null, null, (short) correctCount, accuracy == null ? null : new BigDecimal(accuracy), 7L);
    }

    @Test
    @DisplayName("simulado completo passa na validação")
    void deveAceitarSimuladoValido() {
        assertTrue(violacoes(simulado(10, 90, 7, "70.00")).isEmpty());
    }

    @Test
    @DisplayName("rejeita simulado sem questões")
    void deveRejeitarZeroQuestoes() {
        assertEquals(Set.of("numQuestions deve ser maior que zero"), mensagens(simulado(0, 90, 0, null), "numQuestions"));
    }

    @Test
    @DisplayName("rejeita tempo máximo zero")
    void deveRejeitarTempoZero() {
        assertEquals(Set.of("maxTime deve ser maior que zero"), mensagens(simulado(10, 0, 0, null), "maxTime"));
    }

    @Test
    @DisplayName("rejeita acertos negativos")
    void deveRejeitarAcertosNegativos() {
        assertEquals(Set.of("correctCount não pode ser negativo"), mensagens(simulado(10, 90, -1, null), "correctCount"));
    }

    @Test
    @DisplayName("rejeita accuracy negativa e aceita zero")
    void deveRejeitarAccuracyNegativa() {
        assertEquals(Set.of("accuracy não pode ser negativa"), mensagens(simulado(10, 90, 0, "-0.01"), "accuracy"));
        assertTrue(mensagens(simulado(10, 90, 0, "0"), "accuracy").isEmpty());
    }

    @Test
    @DisplayName("exige o usuário dono do simulado")
    void deveExigirUsuario() {
        MockExamRequestDTO dto = new MockExamRequestDTO("Simulado ENEM", (short) 10, (short) 90,
                null, null, (short) 0, null, null);

        assertEquals(Set.of("userId é obrigatório"), mensagens(dto, "userId"));
    }

    @Test
    @DisplayName("aceita accuracy de 100")
    void deveAceitarAccuracyMaxima() {
        assertTrue(mensagens(simulado(10, 90, 10, "100.00"), "accuracy").isEmpty());
    }

    @Test
    @Pendente("limitar accuracy a 100, por exemplo com @DecimalMax(\"100.00\")")
    @DisplayName("rejeita accuracy acima de 100")
    void deveRejeitarAccuracyAcimaDe100() {
        assertFalse(mensagens(simulado(10, 90, 10, "100.01"), "accuracy").isEmpty());
    }
}
