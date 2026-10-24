package com.pontuo.api_pontuo.dto;

import java.time.LocalDateTime;

public record MockExamHasQuestionResponseDTO(
        Long id,
        LocalDateTime answeredAt,

        // Correção calculada no servidor; só é exibida ao estudante após
        // finalizar o simulado. Continua nula para questões em branco.
        Boolean correct,

        MockExamResponseDTO mockExam,
        QuestionResponseDTO question,
        AnswerOptionResponseDTO answerOption
) {
}
