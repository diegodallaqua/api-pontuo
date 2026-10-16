package com.pontuo.api_pontuo.dto;

import jakarta.validation.constraints.NotNull;

/**
 * {@code answerOptionId} nulo deixa a questão sem resposta. O horário da
 * resposta é registrado pelo servidor.
 */
public record MockExamHasQuestionRequestDTO(
        @NotNull(message = "mockExamId é obrigatório")
        Long mockExamId,

        @NotNull(message = "questionId é obrigatório")
        Long questionId,

        Long answerOptionId
) {
}
