package com.pontuo.api_pontuo.dto;

/**
 * Alternativa embutida na questão sorteada.
 *
 * <p>Existe separada de {@link AnswerOptionResponseDTO} por duas razões: não
 * tem {@code rightAnswer} — o gabarito não pode viajar antes de o estudante
 * responder — e não repete a questão de volta, que aqui seria um ciclo.
 */
public record AnswerOptionSummaryResponseDTO(
        Long id,
        String letter,
        String text
) {
}
