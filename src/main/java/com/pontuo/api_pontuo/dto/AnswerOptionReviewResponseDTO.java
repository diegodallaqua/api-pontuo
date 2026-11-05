package com.pontuo.api_pontuo.dto;

/**
 * Alternativa na revisão do simulado finalizado, agora com o gabarito.
 *
 * <p>Existe separada de {@link AnswerOptionSummaryResponseDTO} porque aquela
 * existe justamente para não carregar {@code rightAnswer}. Aqui o simulado já
 * foi finalizado e o gabarito pode viajar.
 */
public record AnswerOptionReviewResponseDTO(
        Long id,
        String letter,
        String text,
        boolean rightAnswer
) {
}
