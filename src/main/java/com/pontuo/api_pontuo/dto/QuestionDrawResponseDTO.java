package com.pontuo.api_pontuo.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Questão sorteada para um simulado, com alternativas e imagens embutidas.
 *
 * <p>É um contrato próprio, e não {@link QuestionResponseDTO}, por causa do
 * que falta aqui: {@code explanation} é conteúdo de gabarito e só aparece no
 * resultado do simulado. O que sobra é o que o cliente precisa para desenhar
 * a prova sem uma requisição por questão.
 */
public record QuestionDrawResponseDTO(
        Long id,
        String statement,
        Short difficulty,
        LocalDateTime createdAt,
        EntranceExamResponseDTO entranceExam,
        TopicResponseDTO topic,
        SubjectResponseDTO subject,
        List<QuestionImageSummaryResponseDTO> images,
        List<AnswerOptionSummaryResponseDTO> answerOptions
) {
}
