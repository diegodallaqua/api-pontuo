package com.pontuo.api_pontuo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record EntranceExamRequestDTO(
        @NotBlank(message = "name é obrigatório")
        @Size(max = 160, message = "name deve ter no máximo 160 caracteres")
        String name,

        @Positive(message = "year deve ser maior que zero")
        short year,

        @NotBlank(message = "stage é obrigatório")
        @Size(max = 40, message = "stage deve ter no máximo 40 caracteres")
        String stage,

        @NotNull(message = "institutionId é obrigatório")
        Long institutionId
) {
}
