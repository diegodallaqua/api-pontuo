package com.pontuo.api_pontuo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StateRequestDTO(
        @NotBlank(message = "name é obrigatório")
        @Size(max = 100, message = "name deve ter no máximo 100 caracteres")
        String name
) {
}