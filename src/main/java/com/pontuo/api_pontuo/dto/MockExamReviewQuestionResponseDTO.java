package com.pontuo.api_pontuo.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Questão na revisão do simulado finalizado: a resposta do estudante, a
 * questão com a explicação e todas as alternativas com o gabarito.
 *
 * <p>{@code correct} foi congelado na resposta, enquanto {@code rightAnswer}
 * das alternativas é o gabarito atual. Se o gabarito for corrigido depois da
 * finalização, os dois podem divergir.
 */
public record MockExamReviewQuestionResponseDTO(
        Long id,
        LocalDateTime answeredAt,

        // Nulo para questão em branco, assim como selectedAnswerOptionId.
        Boolean correct,

        Long selectedAnswerOptionId,
        QuestionResponseDTO question,
        List<QuestionImageSummaryResponseDTO> images,
        List<AnswerOptionReviewResponseDTO> answerOptions
) {
}
