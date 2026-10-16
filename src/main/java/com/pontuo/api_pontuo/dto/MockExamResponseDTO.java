package com.pontuo.api_pontuo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * {@code answeredCount}, {@code correctCount} e {@code accuracy} ficam nulos
 * até a finalização. {@code accuracy} é correctCount / numQuestions, em
 * porcentagem: questão em branco conta como erro.
 */
public record MockExamResponseDTO(
        Long id,
        String name,
        short numQuestions,
        short maxTime,
        LocalDateTime createdAt,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        Short answeredCount,
        Short correctCount,
        BigDecimal accuracy,
        UserResponseDTO user
) {
}
