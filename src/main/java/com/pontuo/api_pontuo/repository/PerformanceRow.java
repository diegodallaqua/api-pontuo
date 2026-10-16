package com.pontuo.api_pontuo.repository;

/**
 * Uma linha das agregações de desempenho: o agrupamento (área, matéria, tópico
 * ou dificuldade) e as contagens das questões dos simulados finalizados.
 *
 * @param id          id do agrupamento; na dificuldade, o próprio nível
 * @param description descrição do agrupamento; nula na dificuldade
 * @param questions   questões vinculadas
 * @param answered    questões respondidas
 * @param correct     questões corretas
 */
public record PerformanceRow(
        Long id,
        String description,
        Long questions,
        Long answered,
        Long correct
) {
}
