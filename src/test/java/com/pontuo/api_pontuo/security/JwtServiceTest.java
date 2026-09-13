package com.pontuo.api_pontuo.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtService")
class JwtServiceTest {

    private static final String ISSUER = "api-pontuo";
    private static final long EXPIRATION_MINUTES = 120;

    @Mock
    private JwtEncoder encoder;

    @Captor
    private ArgumentCaptor<JwtEncoderParameters> parametros;

    private JwtService service;
    private Authentication loginDeAdministrador;

    @BeforeEach
    void setUp() {
        service = new JwtService(encoder, ISSUER, EXPIRATION_MINUTES);

        loginDeAdministrador = UsernamePasswordAuthenticationToken.authenticated("maria", null, List.of(
                new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"),
                FactorGrantedAuthority.fromAuthority(FactorGrantedAuthority.PASSWORD_AUTHORITY)));

        when(encoder.encode(any())).thenReturn(Jwt.withTokenValue("token-assinado")
                .header("alg", "HS256")
                .subject("maria")
                .build());
    }

    @Test
    @DisplayName("grava emissor, subject, jti e roles que o decoder espera ler")
    void deveGravarClaimsDoContrato() {
        service.issue(loginDeAdministrador);

        verify(encoder).encode(parametros.capture());
        JwtClaimsSet claims = parametros.getValue().getClaims();
        assertAll(
                () -> assertEquals(MacAlgorithm.HS256, parametros.getValue().getJwsHeader().getAlgorithm()),
                () -> assertEquals(ISSUER, claims.getClaimAsString(JwtClaimNames.ISS)),
                () -> assertEquals("maria", claims.getSubject()),
                () -> assertNotNull(claims.getId()),
                () -> assertEquals("ADMINISTRADOR", claims.getClaimAsString(JwtService.ROLES_CLAIM)));
    }

    @Test
    @DisplayName("deixa de fora do claim roles as authorities que não são de perfil")
    void deveIgnorarAuthoritiesSemPrefixoRole() {
        Authentication semPerfil = UsernamePasswordAuthenticationToken.authenticated("joao", null, List.of(
                FactorGrantedAuthority.fromAuthority(FactorGrantedAuthority.PASSWORD_AUTHORITY)));

        service.issue(semPerfil);

        verify(encoder).encode(parametros.capture());
        assertEquals("", parametros.getValue().getClaims().getClaimAsString(JwtService.ROLES_CLAIM));
    }

    @Test
    @DisplayName("expira no prazo configurado e informa o mesmo prazo na resposta")
    void deveExpirarNoPrazoConfigurado() {
        Instant antes = Instant.now();
        JwtService.IssuedToken token = service.issue(loginDeAdministrador);
        Instant depois = Instant.now();

        verify(encoder).encode(parametros.capture());
        JwtClaimsSet claims = parametros.getValue().getClaims();
        assertAll(
                () -> assertEquals(Duration.ofMinutes(EXPIRATION_MINUTES),
                        Duration.between(claims.getIssuedAt(), claims.getExpiresAt())),
                () -> assertFalse(claims.getIssuedAt().isBefore(antes)),
                () -> assertFalse(claims.getIssuedAt().isAfter(depois)),
                () -> assertEquals(claims.getExpiresAt(), token.expiresAt()),
                () -> assertEquals(7200, token.expiresInSeconds()),
                () -> assertEquals("token-assinado", token.value()));
    }

    @Test
    @DisplayName("gera um jti novo a cada token, para o logout de um não derrubar o outro")
    void deveGerarJtiDiferenteACadaToken() {
        service.issue(loginDeAdministrador);
        service.issue(loginDeAdministrador);

        verify(encoder, times(2)).encode(parametros.capture());
        List<JwtEncoderParameters> emitidos = parametros.getAllValues();
        assertNotEquals(emitidos.get(0).getClaims().getId(), emitidos.get(1).getClaims().getId());
    }
}
