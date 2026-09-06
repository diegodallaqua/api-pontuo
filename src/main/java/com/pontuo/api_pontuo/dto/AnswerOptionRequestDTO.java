package com.pontuo.api_pontuo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AnswerOptionRequestDTO(
        @NotBlank(message = "letter é obrigatória")
        @Pattern(regexp = "[A-E]", message = "letter deve ser de A a E")
        String letter,

        @NotBlank(message = "text é obrigatório")
        String text,

        boolean rightAnswer,

        @NotNull(message = "questionId é obrigatório")
        Long questionId
) {
}
