package com.pontuo.api_pontuo.dto;

public record QuestionImageResponseDTO(
        Long id,
        String url,
        String subtitle,
        QuestionResponseDTO question
) {
}
