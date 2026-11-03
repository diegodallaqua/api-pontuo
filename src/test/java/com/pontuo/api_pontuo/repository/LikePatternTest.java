package com.pontuo.api_pontuo.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("LikePattern")
class LikePatternTest {

    @Test
    @DisplayName("sem texto não há filtro")
    void deveIgnorarTextoVazio() {
        assertAll(
                () -> assertNull(LikePattern.contains(null)),
                () -> assertNull(LikePattern.contains("")),
                () -> assertNull(LikePattern.contains("   ")));
    }

    @Test
    @DisplayName("tira os espaços das pontas e passa para minúsculas")
    void deveNormalizar() {
        assertEquals("%florianópolis%", LikePattern.contains("  Florianópolis "));
    }

    @Test
    @DisplayName("escapa os curingas do LIKE e o próprio escape")
    void deveEscaparCuringas() {
        assertAll(
                () -> assertEquals("%100!%%", LikePattern.contains("100%")),
                () -> assertEquals("%a!_b%", LikePattern.contains("a_b")),
                () -> assertEquals("%oi!!%", LikePattern.contains("oi!")));
    }
}
