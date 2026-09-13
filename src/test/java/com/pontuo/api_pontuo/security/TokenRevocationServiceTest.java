package com.pontuo.api_pontuo.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("TokenRevocationService")
class TokenRevocationServiceTest {

    private static final Duration UMA_HORA = Duration.ofHours(1);

    private TokenRevocationService service;

    @BeforeEach
    void setUp() {
        service = new TokenRevocationService();
    }

    @Test
    @DisplayName("token revogado continua bloqueado enquanto não expira")
    void deveBloquearTokenRevogadoAteExpirar() {
        service.revoke(tokenExpirandoEm("jti-1", Instant.now().plus(UMA_HORA)));

        assertTrue(service.isRevoked("jti-1"));
    }

    @Test
    @DisplayName("jti nunca revogado não é considerado revogado")
    void naoDeveBloquearTokenDesconhecido() {
        assertFalse(service.isRevoked("jti-desconhecido"));
    }

    @Test
    @DisplayName("revogação já expirada deixa de valer")
    void naoDeveBloquearDepoisDaExpiracao() {
        service.revoke(tokenExpirandoEm("jti-2", Instant.now().minus(UMA_HORA)));

        assertFalse(service.isRevoked("jti-2"));
    }

    @Test
    @DisplayName("ignora token sem jti em vez de estourar NullPointerException no logout")
    void deveIgnorarTokenSemJti() {
        Jwt semJti = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .subject("maria")
                .expiresAt(Instant.now().plus(UMA_HORA))
                .build();

        assertDoesNotThrow(() -> service.revoke(semJti));
    }

    @Test
    @DisplayName("ignora token sem expiração, que nunca sairia da lista")
    void deveIgnorarTokenSemExpiracao() {
        Jwt semExpiracao = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .jti("jti-3")
                .build();

        assertDoesNotThrow(() -> service.revoke(semExpiracao));
        assertFalse(service.isRevoked("jti-3"));
    }

    private static Jwt tokenExpirandoEm(String jti, Instant expiresAt) {
        return Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .jti(jti)
                .expiresAt(expiresAt)
                .build();
    }
}
