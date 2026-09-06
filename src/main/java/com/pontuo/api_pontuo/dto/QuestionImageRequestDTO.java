package com.pontuo.api_pontuo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record QuestionImageRequestDTO(
        @NotBlank(message = "url é obrigatória")
        @Size(max = 255, message = "url deve ter no máximo 255 caracteres")
        String url,

        @Size(max = 255, message = "subtitle deve ter no máximo 255 caracteres")
        String subtitle,

        @NotNull(message = "questionId é obrigatório")
        Long questionId
) {
}
