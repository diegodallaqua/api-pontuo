package com.pontuo.api_pontuo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

/**
 * Arquivo que o frontend vai enviar ao bucket. Tipo e tamanho entram na
 * assinatura da URL, então precisam ser os do arquivo real.
 */
public record QuestionImageUploadRequestDTO(
        @NotNull(message = "contentType é obrigatório")
        @Pattern(regexp = "image/(png|jpeg|webp|gif)",
                message = "contentType deve ser image/png, image/jpeg, image/webp ou image/gif")
        String contentType,

        @NotNull(message = "size é obrigatório")
        @Positive(message = "size deve ser maior que zero")
        @Max(value = 5_242_880,
                message = "size deve ser no máximo 5242880 bytes (5 MB)")
        Long size
) {
}
