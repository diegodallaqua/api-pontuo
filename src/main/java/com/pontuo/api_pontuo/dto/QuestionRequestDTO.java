package com.pontuo.api_pontuo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QuestionRequestDTO(
        @NotBlank(message = "statement é obrigatório")
        String statement,

        String explanation,

        @Min(value = 1, message = "difficulty deve estar entre 1 e 3")
        @Max(value = 3, message = "difficulty deve estar entre 1 e 3")
        Short difficulty,

        @NotNull(message = "entranceExamId é obrigatório")
        Long entranceExamId,

        Long topicId,

        Long subjectId
) {
}
