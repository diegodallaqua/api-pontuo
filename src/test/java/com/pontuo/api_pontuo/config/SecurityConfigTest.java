package com.pontuo.api_pontuo.config;

import com.pontuo.api_pontuo.security.JwtService;
import com.pontuo.api_pontuo.security.RevokedTokenValidator;
import com.pontuo.api_pontuo.security.TokenRevocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("SecurityConfig")
class SecurityConfigTest {

    private static final String ISSUER = "api-pontuo";
    private static final String SECRET = "segredo-de-teste-com-mais-de-32-bytes!!";

    private final SecurityConfig config = new SecurityConfig();

    @Nested
    @DisplayName("jwtSecretKey")
    class JwtSecretKey {

        @Test
        @DisplayName("impede a aplicação de subir com segredo de 31 bytes")
        void deveRecusarSegredoCurto() {
            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> config.jwtSecretKey("a".repeat(31)));

            assertEquals("security.jwt.secret deve ter no mínimo 32 caracteres.", ex.getMessage());
        }

        @Test
        @DisplayName("aceita segredo de exatamente 32 bytes como chave HmacSHA256")
        void deveAceitarSegredoNoLimite() {
            SecretKey chave = config.jwtSecretKey("a".repeat(32));

            assertEquals("HmacSHA256", chave.getAlgorithm());
        }

        @Test
        @DisplayName("mede bytes, não caracteres: 16 letras acentuadas somam 32 bytes e passam")
        void deveMedirTamanhoEmBytes() {
            assertDoesNotThrow(() -> config.jwtSecretKey("é".repeat(16)));
        }
    }

    @Nested
    @DisplayName("jwtDecoder")
    class Decoder {

        private JwtEncoder encoder;
        private TokenRevocationService revocationService;
        private JwtDecoder decoder;

        @BeforeEach
        void setUp() {
            SecretKey chave = config.jwtSecretKey(SECRET);
            encoder = config.jwtEncoder(chave);
            revocationService = new TokenRevocationService();
            decoder = config.jwtDecoder(chave, ISSUER, new RevokedTokenValidator(revocationService));
        }

        @Test
        @DisplayName("aceita token assinado com a chave e o emissor da API")
        void deveAceitarTokenValido() {
            Jwt jwt = decoder.decode(assinar(encoder, claimsValidas().build()));

            assertEquals("maria", jwt.getSubject());
        }

        @Test
        @DisplayName("recusa token assinado com outra chave")
        void deveRecusarAssinaturaDeOutraChave() {
            JwtEncoder outraChave = config.jwtEncoder(config.jwtSecretKey("outro-segredo-com-mais-de-32-bytes!!"));
            String token = assinar(outraChave, claimsValidas().build());

            assertThrows(BadJwtException.class, () -> decoder.decode(token));
        }

        @Test
        @DisplayName("recusa token de outro emissor")
        void deveRecusarOutroEmissor() {
            String token = assinar(encoder, claimsValidas().issuer("outra-api").build());

            assertThrows(BadJwtException.class, () -> decoder.decode(token));
        }

        @Test
        @DisplayName("recusa token sem jti")
        void deveRecusarTokenSemJti() {
            String token = assinar(encoder, claimsValidas().claims(claims -> claims.remove(JwtClaimNames.JTI)).build());

            assertThrows(BadJwtException.class, () -> decoder.decode(token));
        }

        @Test
        @DisplayName("recusa token que passou pelo logout")
        void deveRecusarTokenRevogado() {
            String token = assinar(encoder, claimsValidas().build());
            revocationService.revoke(decoder.decode(token));

            assertThrows(BadJwtException.class, () -> decoder.decode(token));
        }

        @Test
        @DisplayName("recusa token expirado há mais que a tolerância de 60 s")
        void deveRecusarTokenExpirado() {
            Instant agora = Instant.now();
            String token = assinar(encoder, claimsValidas()
                    .issuedAt(agora.minus(Duration.ofHours(2)))
                    .expiresAt(agora.minus(Duration.ofHours(1)))
                    .build());

            assertThrows(BadJwtException.class, () -> decoder.decode(token));
        }

        @Test
        @DisplayName("ainda aceita token expirado há 30 s, dentro da tolerância de relógio")
        void deveAceitarTokenDentroDaTolerancia() {
            Instant agora = Instant.now();
            String token = assinar(encoder, claimsValidas()
                    .issuedAt(agora.minus(Duration.ofMinutes(10)))
                    .expiresAt(agora.minus(Duration.ofSeconds(30)))
                    .build());

            assertDoesNotThrow(() -> decoder.decode(token));
        }
    }

    @Nested
    @DisplayName("ida e volta")
    class IdaEVolta {

        @Test
        @DisplayName("token do JwtService é aceito pelo decoder e vira ROLE_ADMINISTRADOR no conversor")
        void deveIntegrarEmissaoLeituraEConversao() {
            SecretKey chave = config.jwtSecretKey(SECRET);
            JwtService jwtService = new JwtService(config.jwtEncoder(chave), ISSUER, 120);
            JwtDecoder decoder = config.jwtDecoder(chave, ISSUER, new RevokedTokenValidator(new TokenRevocationService()));
            Authentication login = UsernamePasswordAuthenticationToken.authenticated("maria", null, List.of(
                    new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"),
                    FactorGrantedAuthority.fromAuthority(FactorGrantedAuthority.PASSWORD_AUTHORITY)));

            Jwt jwt = decoder.decode(jwtService.issue(login).value());
            AbstractAuthenticationToken autenticacao = config.jwtAuthenticationConverter().convert(jwt);

            assertAll(
                    () -> assertEquals("maria", autenticacao.getName()),
                    () -> assertEquals(Set.of("ROLE_ADMINISTRADOR"), roles(autenticacao)));
        }

        private Set<String> roles(Authentication autenticacao) {
            return autenticacao.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(authority -> authority.startsWith("ROLE_"))
                    .collect(Collectors.toSet());
        }
    }

    private static JwtClaimsSet.Builder claimsValidas() {
        Instant agora = Instant.now();
        return JwtClaimsSet.builder()
                .issuer(ISSUER)
                .subject("maria")
                .id(UUID.randomUUID().toString())
                .issuedAt(agora)
                .expiresAt(agora.plus(Duration.ofHours(1)))
                .claim(JwtService.ROLES_CLAIM, "ESTUDANTE");
    }

    private static String assinar(JwtEncoder encoder, JwtClaimsSet claims) {
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
