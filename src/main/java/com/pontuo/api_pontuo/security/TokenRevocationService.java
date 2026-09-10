package com.pontuo.api_pontuo.security;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lista de tokens invalidados pelo logout, indexada pelo claim jti.
 *
 * O JWT é stateless: sem esta lista, um token continuaria válido até expirar
 * mesmo depois do logout. O armazenamento é em memória, então a lista é perdida
 * no restart e não é compartilhada entre instâncias da aplicação. Para vários
 * nós, trocar por um store compartilhado (Redis ou tabela dedicada).
 */
@Service
public class TokenRevocationService {

    private final Map<String, Instant> revokedByTokenId = new ConcurrentHashMap<>();

    public void revoke(Jwt jwt) {
        String tokenId = jwt.getId();
        Instant expiresAt = jwt.getExpiresAt();
        if (tokenId == null || expiresAt == null) {
            return;
        }
        purgeExpired();
        revokedByTokenId.put(tokenId, expiresAt);
    }

    public boolean isRevoked(String tokenId) {
        Instant expiresAt = revokedByTokenId.get(tokenId);
        return expiresAt != null && expiresAt.isAfter(Instant.now());
    }

    /**
     * Remove entradas de tokens que já expiraram: depois da expiração o próprio
     * decoder rejeita o token, logo guardá-lo só consumiria memória.
     */
    private void purgeExpired() {
        Instant now = Instant.now();
        revokedByTokenId.values().removeIf(expiresAt -> expiresAt.isBefore(now));
    }
}
