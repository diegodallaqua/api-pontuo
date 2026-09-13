package com.pontuo.api_pontuo.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JwtService {

    public static final String ROLES_CLAIM = "roles";

    private static final JwsHeader HEADER = JwsHeader.with(MacAlgorithm.HS256).build();
    private static final String ROLE_PREFIX = "ROLE_";

    private final JwtEncoder encoder;
    private final String issuer;
    private final Duration expiration;

    public JwtService(JwtEncoder encoder,
                      @Value("${security.jwt.issuer}") String issuer,
                      @Value("${security.jwt.expiration-minutes}") long expirationMinutes) {
        this.encoder = encoder;
        this.issuer = issuer;
        this.expiration = Duration.ofMinutes(expirationMinutes);
    }

    public IssuedToken issue(Authentication authentication) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(expiration);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(authentication.getName())
                // jti identifica o token para que o logout possa revogá-lo.
                .id(UUID.randomUUID().toString())
                .claim(ROLES_CLAIM, authorities(authentication))
                .build();

        String value = encoder.encode(JwtEncoderParameters.from(HEADER, claims)).getTokenValue();
        return new IssuedToken(value, expiresAt, expiration.toSeconds());
    }

    private String authorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith(ROLE_PREFIX))
                .map(authority -> authority.substring(ROLE_PREFIX.length()))
                .collect(Collectors.joining(" "));
    }

    public record IssuedToken(String value, Instant expiresAt, long expiresInSeconds) {
    }
}
