package com.pontuo.api_pontuo.dto;

import java.time.LocalDateTime;

public record MockExamHasQuestionResponseDTO(
        Long id,
        LocalDateTime answeredAt,
        MockExamResponseDTO mockExam,
        QuestionResponseDTO question,
        AnswerOptionResponseDTO answerOption
) {
}
