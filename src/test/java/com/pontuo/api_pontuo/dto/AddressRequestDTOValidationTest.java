package com.pontuo.api_pontuo.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("AddressRequestDTO")
class AddressRequestDTOValidationTest extends AbstractDtoValidationTest {

    private static AddressRequestDTO comNumero(Integer numero) {
        return new AddressRequestDTO("Rua dos Andradas", "Santa Ifigênia", numero, null, 5285L);
    }

    @Test
    @DisplayName("aceita o menor número válido")
    void deveAceitarNumeroUm() {
        assertTrue(violacoes(comNumero(1)).isEmpty());
    }

    @Test
    @DisplayName("rejeita número zero, como o do endereço 10 antes da migration V3")
    void deveRejeitarNumeroZero() {
        assertEquals(Set.of("number deve ser maior que zero"), mensagens(comNumero(0), "number"));
    }

    @Test
    @DisplayName("exige o número")
    void deveExigirNumero() {
        assertEquals(Set.of("number é obrigatório"), mensagens(comNumero(null), "number"));
    }

    @Test
    @DisplayName("exige a cidade")
    void deveExigirCidade() {
        AddressRequestDTO dto = new AddressRequestDTO("Rua dos Andradas", "Santa Ifigênia", 140, null, null);

        assertEquals(Set.of("cityId é obrigatório"), mensagens(dto, "cityId"));
    }
}
