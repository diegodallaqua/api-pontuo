package com.pontuo.api_pontuo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AddressRequestDTO(
        @NotBlank(message = "street é obrigatório")
        @Size(max = 180, message = "street deve ter no máximo 180 caracteres")
        String street,

        @NotBlank(message = "neighborhood é obrigatório")
        @Size(max = 120, message = "neighborhood deve ter no máximo 120 caracteres")
        String neighborhood,

        @NotNull(message = "number é obrigatório")
        @Positive(message = "number deve ser maior que zero")
        Integer number,

        @Size(max = 120, message = "complement deve ter no máximo 120 caracteres")
        String complement,

        @NotNull(message = "cityId é obrigatório")
        Long cityId
) {
}
