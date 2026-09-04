package com.pontuo.api_pontuo.dto;

public record InstitutionResponseDTO(
        Long id,
        String name,
        String acronym,
        AddressResponseDTO address
) {
}