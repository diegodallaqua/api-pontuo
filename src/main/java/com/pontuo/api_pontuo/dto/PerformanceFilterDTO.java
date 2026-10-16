package com.pontuo.api_pontuo.dto;

import java.time.LocalDate;

/**
 * Filtros do desempenho, todos opcionais. O período vale sobre a data de
 * finalização do simulado, com {@code from} e {@code to} inclusivos.
 * {@code entranceExamId} restringe às questões daquele vestibular e só vale
 * nos detalhamentos por área, matéria, tópico e dificuldade.
 */
public record PerformanceFilterDTO(
        LocalDate from,
        LocalDate to,
        Long entranceExamId
) {
}
