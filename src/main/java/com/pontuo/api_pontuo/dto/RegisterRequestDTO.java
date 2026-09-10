package com.pontuo.api_pontuo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Cadastro público. Não tem userRoleId de propósito: se o cliente pudesse
 * escolher o perfil, qualquer pessoa criaria uma conta de administrador.
 * A role é definida pelo servidor.
 */
public record RegisterRequestDTO(
        @NotBlank(message = "username é obrigatório")
        @Size(max = 60, message = "username deve ter no máximo 60 caracteres")
        String username,

        @NotBlank(message = "email é obrigatório")
        @Email(message = "email deve ser válido")
        @Size(max = 160, message = "email deve ter no máximo 160 caracteres")
        String email,

        @NotNull(message = "birthDate é obrigatório")
        @Past(message = "birthDate deve ser uma data no passado")
        LocalDate birthDate,

        @NotBlank(message = "password é obrigatório")
        @Size(min = 8, max = 72, message = "password deve ter entre 8 e 72 caracteres")
        String password
) {
}
