package com.pontuo.api_pontuo.dto;

/**
 * Imagem embutida na questão sorteada. Como a alternativa, não repete a
 * questão de volta.
 */
public record QuestionImageSummaryResponseDTO(
        Long id,
        String url,
        String subtitle
) {
}
