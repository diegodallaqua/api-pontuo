package com.pontuo.api_pontuo.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Nomes das roles da API e a checagem usada quando a resposta muda conforme o
 * perfil de quem pediu — e não só o acesso à rota. O caso é o gabarito das
 * alternativas, que sai apenas para o administrador.
 */
public final class Roles {

    /** Sem o prefixo {@code ROLE_}, como {@code hasRole} espera. */
    public static final String ADMINISTRADOR = "ADMINISTRADOR";

    private static final String AUTHORITY_ADMINISTRADOR = "ROLE_" + ADMINISTRADOR;

    private Roles() {
    }

    public static boolean isAdministrador() {
        return isAdministrador(SecurityContextHolder.getContext().getAuthentication());
    }

    public static boolean isAdministrador(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(AUTHORITY_ADMINISTRADOR::equals);
    }
}
