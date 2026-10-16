package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.AnswerOption;
import com.pontuo.api_pontuo.entity.Question;
import com.pontuo.api_pontuo.entity.QuestionImage;

import java.util.List;

/**
 * Questão sorteada junto com o que o cliente precisa para exibi-la.
 *
 * <p>As alternativas e imagens andam ao lado da entidade, e não dentro dela,
 * porque {@link Question} não mapeia essas coleções: mapeá-las tornaria toda
 * leitura de questão candidata a arrastar a prova inteira atrás.
 */
public record DrawnQuestion(
        Question question,
        List<AnswerOption> answerOptions,
        List<QuestionImage> images
) {
}
