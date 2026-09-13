package com.pontuo.api_pontuo.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("RegisterRequestDTO")
class RegisterRequestDTOValidationTest extends AbstractDtoValidationTest {

    private static final LocalDate NASCIMENTO = LocalDate.of(2001, 3, 9);

    @Test
    @DisplayName("cadastro completo passa na validação")
    void deveAceitarCadastroValido() {
        assertTrue(violacoes(new RegisterRequestDTO("maria", "maria@pontuo.com", NASCIMENTO, "SenhaForte123")).isEmpty());
    }

    @ParameterizedTest(name = "senha com {0} caracteres é rejeitada")
    @ValueSource(ints = {7, 73})
    void deveRejeitarSenhaForaDosLimites(int tamanho) {
        RegisterRequestDTO dto = new RegisterRequestDTO("maria", "maria@pontuo.com", NASCIMENTO, "a".repeat(tamanho));

        assertEquals(Set.of("password deve ter entre 8 e 72 caracteres"), mensagens(dto, "password"));
    }

    @ParameterizedTest(name = "senha com {0} caracteres é aceita")
    @ValueSource(ints = {8, 72})
    void deveAceitarSenhaNosLimites(int tamanho) {
        RegisterRequestDTO dto = new RegisterRequestDTO("maria", "maria@pontuo.com", NASCIMENTO, "a".repeat(tamanho));

        assertTrue(mensagens(dto, "password").isEmpty());
    }

    @Test
    @DisplayName("rejeita email sem @")
    void deveRejeitarEmailInvalido() {
        RegisterRequestDTO dto = new RegisterRequestDTO("maria", "maria.pontuo.com", NASCIMENTO, "SenhaForte123");

        assertEquals(Set.of("email deve ser válido"), mensagens(dto, "email"));
    }

    @Test
    @DisplayName("rejeita username com mais de 60 caracteres")
    void deveRejeitarUsernameLongo() {
        RegisterRequestDTO dto = new RegisterRequestDTO("a".repeat(61), "maria@pontuo.com", NASCIMENTO, "SenhaForte123");

        assertEquals(Set.of("username deve ter no máximo 60 caracteres"), mensagens(dto, "username"));
    }

    @Test
    @DisplayName("rejeita nascimento hoje: @Past não inclui o dia atual")
    void deveRejeitarNascimentoHoje() {
        RegisterRequestDTO dto = new RegisterRequestDTO("maria", "maria@pontuo.com", HOJE, "SenhaForte123");

        assertEquals(Set.of("birthDate deve ser uma data no passado"), mensagens(dto, "birthDate"));
    }

    @Test
    @DisplayName("aceita nascimento ontem")
    void deveAceitarNascimentoOntem() {
        RegisterRequestDTO dto = new RegisterRequestDTO("maria", "maria@pontuo.com", HOJE.minusDays(1), "SenhaForte123");

        assertTrue(mensagens(dto, "birthDate").isEmpty());
    }
}
