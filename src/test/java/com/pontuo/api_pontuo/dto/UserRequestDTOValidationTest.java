package com.pontuo.api_pontuo.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UserRequestDTO")
class UserRequestDTOValidationTest extends AbstractDtoValidationTest {

    private static final LocalDate NASCIMENTO = LocalDate.of(2001, 3, 9);

    @Test
    @DisplayName("usuário completo passa na validação")
    void deveAceitarUsuarioValido() {
        assertTrue(violacoes(new UserRequestDTO("maria", "maria@pontuo.com", NASCIMENTO, "SenhaForte123", 2L)).isEmpty());
    }

    @Test
    @DisplayName("exige o perfil do usuário")
    void deveExigirPerfil() {
        UserRequestDTO dto = new UserRequestDTO("maria", "maria@pontuo.com", NASCIMENTO, "SenhaForte123", null);

        assertEquals(Set.of("userRoleId é obrigatório"), mensagens(dto, "userRoleId"));
    }

    @ParameterizedTest(name = "senha com {0} caracteres é rejeitada, como no cadastro público")
    @ValueSource(ints = {7, 73})
    void deveAplicarMesmosLimitesDeSenha(int tamanho) {
        UserRequestDTO dto = new UserRequestDTO("maria", "maria@pontuo.com", NASCIMENTO, "a".repeat(tamanho), 2L);

        assertEquals(Set.of("password deve ter entre 8 e 72 caracteres"), mensagens(dto, "password"));
    }

    @Test
    @DisplayName("rejeita nascimento hoje")
    void deveRejeitarNascimentoHoje() {
        UserRequestDTO dto = new UserRequestDTO("maria", "maria@pontuo.com", HOJE, "SenhaForte123", 2L);

        assertEquals(Set.of("birthDate deve ser uma data no passado"), mensagens(dto, "birthDate"));
    }
}
