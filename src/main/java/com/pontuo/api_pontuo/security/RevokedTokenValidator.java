package com.pontuo.api_pontuo.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Estratégia de validação de token (<b>Strategy</b>): recusa JWTs cujo
 * {@code jti} foi registrado no logout. O {@code JwtDecoder} a combina com as
 * validações padrão de expiração e emissor por meio de
 * {@code DelegatingOAuth2TokenValidator}, sem conhecer a regra aplicada aqui.
 */
@Component
public class RevokedTokenValidator implements OAuth2TokenValidator<Jwt> {

    private static final OAuth2Error REVOKED = new OAuth2Error(
            "invalid_token", "O token não é mais válido.", null);

    private final TokenRevocationService revocationService;

    public RevokedTokenValidator(TokenRevocationService revocationService) {
        this.revocationService = revocationService;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        String tokenId = token.getId();
        if (tokenId == null || revocationService.isRevoked(tokenId)) {
            return OAuth2TokenValidatorResult.failure(REVOKED);
        }
        return OAuth2TokenValidatorResult.success();
    }
}
