package com.pontuo.api_pontuo.dto;

public record SubjectResponseDTO(
        Long id,
        String description,
        KnowledgeAreaResponseDTO knowledgeArea
) {
}