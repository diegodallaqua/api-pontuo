package com.pontuo.api_pontuo.dto;

public record CityResponseDTO(
        Long id,
        String name,
        StateResponseDTO state
) {
}