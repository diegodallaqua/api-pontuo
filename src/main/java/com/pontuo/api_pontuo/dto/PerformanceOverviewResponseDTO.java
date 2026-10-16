package com.pontuo.api_pontuo.dto;

import java.math.BigDecimal;

/**
 * Resumo dos simulados finalizados no período.
 *
 * @param finishedMockExams      simulados finalizados
 * @param questions              soma de numQuestions
 * @param answered               questões respondidas
 * @param correct                questões corretas
 * @param accuracy               correct / questions, em porcentagem; nulo sem simulados
 * @param averageDurationMinutes duração média entre início e fim; nula sem simulados
 */
public record PerformanceOverviewResponseDTO(
        long finishedMockExams,
        long questions,
        long answered,
        long correct,
        BigDecimal accuracy,
        Long averageDurationMinutes
) {
}
