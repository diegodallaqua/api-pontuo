package com.pontuo.api_pontuo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Início, fim, acertos e aproveitamento não vêm do cliente: são definidos pelo
 * servidor em POST /{id}/start e POST /{id}/finish.
 *
 * <p>{@code userId} é opcional. Sem ele, o dono é o usuário do token; só o
 * administrador pode informar outra pessoa.
 */
public record MockExamRequestDTO(
        @NotBlank(message = "name é obrigatório")
        @Size(max = 150, message = "name deve ter no máximo 150 caracteres")
        String name,

        @Positive(message = "numQuestions deve ser maior que zero")
        short numQuestions,

        @Positive(message = "maxTime deve ser maior que zero")
        short maxTime,

        Long userId
) {
}
