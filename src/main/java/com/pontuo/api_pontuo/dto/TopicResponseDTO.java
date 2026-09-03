package com.pontuo.api_pontuo.dto;

public record TopicResponseDTO(
        Long id,
        String description,
        SubjectResponseDTO subject
) {
}