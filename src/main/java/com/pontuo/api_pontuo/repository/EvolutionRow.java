package com.pontuo.api_pontuo.repository;

import java.time.LocalDateTime;

/**
 * Um ponto da evolução em uma matéria: as contagens de um simulado finalizado
 * restritas às questões daquela matéria.
 */
public record EvolutionRow(
        Long mockExamId,
        String mockExamName,
        LocalDateTime finishedAt,
        Long questions,
        Long answered,
        Long correct
) {
}
