package com.pontuo.api_pontuo.security;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    private void purgeExpired() {
        Instant now = Instant.now();
        revokedByTokenId.values().removeIf(expiresAt -> expiresAt.isBefore(now));
    }
}
