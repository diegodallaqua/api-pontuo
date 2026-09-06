package com.pontuo.api_pontuo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MockExamRequestDTO(
        @NotBlank(message = "name é obrigatório")
        @Size(max = 150, message = "name deve ter no máximo 150 caracteres")
        String name,

        @Positive(message = "numQuestions deve ser maior que zero")
        short numQuestions,

        @Positive(message = "maxTime deve ser maior que zero")
        short maxTime,

        LocalDateTime startedAt,

        LocalDateTime finishedAt,

        @PositiveOrZero(message = "correctCount não pode ser negativo")
        short correctCount,

        @PositiveOrZero(message = "accuracy não pode ser negativa")
        BigDecimal accuracy,

        @NotNull(message = "userId é obrigatório")
        Long userId
) {
}
