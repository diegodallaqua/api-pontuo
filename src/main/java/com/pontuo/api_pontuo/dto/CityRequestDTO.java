package com.pontuo.api_pontuo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CityRequestDTO(
        @NotBlank(message = "name é obrigatório")
        @Size(max = 120, message = "name deve ter no máximo 120 caracteres")
        String name,

        @NotNull(message = "stateId é obrigatório")
        Long stateId
) {
}
