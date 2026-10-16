package com.pontuo.api_pontuo.dto;

import java.math.BigDecimal;

/**
 * Desempenho em um agrupamento: área do conhecimento, matéria, tópico ou
 * dificuldade. {@code accuracy} é correct / questions, em porcentagem.
 * Na dificuldade, {@code id} é o nível (1 a 3) e {@code description} é nula.
 */
public record PerformanceItemResponseDTO(
        Long id,
        String description,
        long questions,
        long answered,
        long correct,
        BigDecimal accuracy
) {
}
