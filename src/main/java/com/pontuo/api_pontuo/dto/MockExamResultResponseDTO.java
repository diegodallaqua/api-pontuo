package com.pontuo.api_pontuo.dto;

import java.util.List;

/**
 * Boletim do simulado finalizado: totais e aproveitamento por matéria. Em cada
 * matéria, {@code accuracy} é correct / questions vinculadas daquela matéria.
 */
public record MockExamResultResponseDTO(
        MockExamResponseDTO mockExam,
        List<PerformanceItemResponseDTO> subjects
) {
}
