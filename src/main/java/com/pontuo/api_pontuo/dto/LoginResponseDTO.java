package com.pontuo.api_pontuo.dto;

public record LoginResponseDTO(
        String tokenType,
        String accessToken,
        long expiresIn,
        UserResponseDTO user
) {
}
