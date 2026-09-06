package com.pontuo.api_pontuo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MockExamResponseDTO(
        Long id,
        String name,
        short numQuestions,
        short maxTime,
        LocalDateTime createdAt,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        short correctCount,
        BigDecimal accuracy,
        UserResponseDTO user
) {
}
