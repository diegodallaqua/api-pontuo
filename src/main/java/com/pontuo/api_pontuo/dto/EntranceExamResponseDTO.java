package com.pontuo.api_pontuo.dto;

public record EntranceExamResponseDTO(
        Long id,
        String name,
        short year,
        String stage,
        InstitutionResponseDTO institution
) {
}