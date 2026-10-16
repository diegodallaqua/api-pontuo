package com.pontuo.api_pontuo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

public record AnswerOptionResponseDTO(
        Long id,
        String letter,
        String text,

        // Gabarito. Só é preenchido para o administrador; para o estudante o
        // campo nem aparece no JSON, senão a resposta certa vazaria antes de
        // ele responder. A correção do estudante vem no campo correct de
        // MockExamHasQuestionResponseDTO.
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Boolean rightAnswer,

        QuestionResponseDTO question
) {
}
