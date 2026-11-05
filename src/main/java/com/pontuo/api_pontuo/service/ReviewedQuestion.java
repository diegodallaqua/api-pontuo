package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.AnswerOption;
import com.pontuo.api_pontuo.entity.MockExamHasQuestion;
import com.pontuo.api_pontuo.entity.QuestionImage;

import java.util.List;

/**
 * Questão de um simulado finalizado, com a resposta do estudante e o que a
 * revisão precisa exibir.
 *
 * <p>Mesmo arranjo de {@link DrawnQuestion}: alternativas e imagens andam ao
 * lado do vínculo porque a questão não mapeia essas coleções.
 */
public record ReviewedQuestion(
        MockExamHasQuestion link,
        List<AnswerOption> answerOptions,
        List<QuestionImage> images
) {
}
