package com.pontuo.api_pontuo.dto;

public record HealthResponseDTO(String status) {

    public static final HealthResponseDTO UP = new HealthResponseDTO("UP");
}
