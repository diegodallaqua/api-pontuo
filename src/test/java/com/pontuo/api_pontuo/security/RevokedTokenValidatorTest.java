package com.pontuo.api_pontuo.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RevokedTokenValidator")
class RevokedTokenValidatorTest {

    @Mock
    private TokenRevocationService revocationService;

    @InjectMocks
    private RevokedTokenValidator validator;

    @Test
    @DisplayName("recusa token sem jti, que não poderia ser revogado")
    void deveRecusarTokenSemJti() {
        Jwt semJti = Jwt.withTokenValue("token").header("alg", "HS256").subject("maria").build();

        OAuth2TokenValidatorResult result = validator.validate(semJti);

        assertEquals(List.of("invalid_token"), codigosDeErro(result));
        verifyNoInteractions(revocationService);
    }

    @Test
    @DisplayName("recusa token que passou pelo logout")
    void deveRecusarTokenRevogado() {
        when(revocationService.isRevoked("jti-1")).thenReturn(true);

        OAuth2TokenValidatorResult result = validator.validate(tokenComJti("jti-1"));

        assertEquals(List.of("invalid_token"), codigosDeErro(result));
    }

    @Test
    @DisplayName("aceita token com jti que não foi revogado")
    void deveAceitarTokenValido() {
        when(revocationService.isRevoked("jti-1")).thenReturn(false);

        OAuth2TokenValidatorResult result = validator.validate(tokenComJti("jti-1"));

        assertFalse(result.hasErrors());
    }

    private static Jwt tokenComJti(String jti) {
        return Jwt.withTokenValue("token").header("alg", "HS256").jti(jti).build();
    }

    private static List<String> codigosDeErro(OAuth2TokenValidatorResult result) {
        return result.getErrors().stream().map(OAuth2Error::getErrorCode).toList();
    }
}
