package com.pontuo.api_pontuo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRoleRequestDTO(
        @NotBlank(message = "description é obrigatório")
        @Size(max = 50, message = "description deve ter no máximo 50 caracteres")
        String description
) {
}