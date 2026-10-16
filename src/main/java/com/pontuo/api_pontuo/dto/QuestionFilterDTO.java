package com.pontuo.api_pontuo.dto;

/**
 * Filtros da busca de questões. Todos são opcionais e se combinam: os que vêm
 * preenchidos são aplicados juntos, em AND.
 *
 * <p>Como a questão é classificada por tópico OU por matéria, {@code subjectId}
 * e {@code knowledgeAreaId} valem pelos dois caminhos: a matéria (ou área) da
 * própria questão e a do tópico dela.
 */
public record QuestionFilterDTO(
        Long entranceExamId,
        Long knowledgeAreaId,
        Long subjectId,
        Long topicId,
        Short difficulty
) {
}
