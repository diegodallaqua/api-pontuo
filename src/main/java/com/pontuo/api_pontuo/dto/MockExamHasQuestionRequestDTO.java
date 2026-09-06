package com.pontuo.api_pontuo.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record MockExamHasQuestionRequestDTO(
        @NotNull(message = "mockExamId é obrigatório")
        Long mockExamId,

        @NotNull(message = "questionId é obrigatório")
        Long questionId,

        Long answerOptionId,

        LocalDateTime answeredAt
) {
}
