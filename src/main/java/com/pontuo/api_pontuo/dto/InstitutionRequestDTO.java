package com.pontuo.api_pontuo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InstitutionRequestDTO(
        @NotBlank(message = "name é obrigatório")
        @Size(max = 160, message = "name deve ter no máximo 180 caracteres")
        String name,

        @NotBlank(message = "acronym é obrigatório")
        @Size(max = 20, message = "acronym deve ter no máximo 120 caracteres")
        String acronym,

        @NotNull(message = "addressId é obrigatório")
        Long addressId
) {
}
