package com.pontuo.api_pontuo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Um ponto da evolução: o resultado de um simulado finalizado. */
public record EvolutionPointResponseDTO(
        Long mockExamId,
        String mockExamName,
        LocalDateTime finishedAt,
        long questions,
        long answered,
        long correct,
        BigDecimal accuracy
) {
}
