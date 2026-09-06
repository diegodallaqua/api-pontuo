package com.pontuo.api_pontuo.dto;

public record AnswerOptionResponseDTO(
        Long id,
        String letter,
        String text,
        boolean rightAnswer,
        QuestionResponseDTO question
) {
}
