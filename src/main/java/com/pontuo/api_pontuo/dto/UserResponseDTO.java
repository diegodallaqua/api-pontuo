package com.pontuo.api_pontuo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserResponseDTO(
        Long id,
        String username,
        String email,
        LocalDate birthDate,
        LocalDateTime createdAt,
        UserRoleResponseDTO userRole
) {
}
