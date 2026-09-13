package com.pontuo.api_pontuo.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("EntranceExamRequestDTO")
class EntranceExamRequestDTOValidationTest extends AbstractDtoValidationTest {

    private static EntranceExamRequestDTO comEtapa(String etapa) {
        return new EntranceExamRequestDTO("ENEM", (short) 2026, etapa, 1L);
    }

    @Test
    @DisplayName("vestibular completo passa na validação")
    void deveAceitarVestibularValido() {
        assertTrue(violacoes(comEtapa("1º Dia")).isEmpty());
    }

    @ParameterizedTest(name = "etapa [{0}] é rejeitada")
    @DisplayName("exige a etapa, que faz parte da chave única do vestibular")
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void deveExigirEtapa(String etapa) {
        assertEquals(Set.of("stage é obrigatório"), mensagens(comEtapa(etapa), "stage"));
    }

    @Test
    @DisplayName("rejeita etapa com mais de 40 caracteres")
    void deveRejeitarEtapaLonga() {
        assertEquals(Set.of("stage deve ter no máximo 40 caracteres"), mensagens(comEtapa("a".repeat(41)), "stage"));
    }
}
