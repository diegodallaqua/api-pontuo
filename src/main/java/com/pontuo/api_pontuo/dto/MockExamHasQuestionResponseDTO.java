package com.pontuo.api_pontuo.dto;

import java.time.LocalDateTime;

public record MockExamHasQuestionResponseDTO(
        Long id,
        LocalDateTime answeredAt,

        // Correção da alternativa marcada, calculada no servidor. Fica nula
        // enquanto a questão não foi respondida — é assim que o estudante sabe
        // se acertou sem receber o gabarito das alternativas.
        Boolean correct,

        MockExamResponseDTO mockExam,
        QuestionResponseDTO question,
        AnswerOptionResponseDTO answerOption
) {
}
