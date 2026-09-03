package com.pontuo.api_pontuo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TopicRequestDTO(
        @NotBlank(message = "description é obrigatório")
        @Size(max = 120, message = "description deve ter no máximo 120 caracteres")
        String description,

        @NotNull(message = "subjectId é obrigatório")
        Long subjectId
) {
}
