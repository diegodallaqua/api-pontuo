package com.pontuo.api_pontuo.security;

import com.pontuo.api_pontuo.entity.User;
import com.pontuo.api_pontuo.entity.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.text.Normalizer;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Adapta a entidade User para o contrato que o Spring Security espera.
 * A senha exposta aqui é sempre o hash BCrypt gravado no banco.
 */
public class AppUserDetails implements UserDetails {

    private final User user;

    public AppUserDetails(User user) {
        this.user = user;
    }

    public User user() {
        return user;
    }

    /**
     * Converte a descrição da UserRole em authority: "Administrador" vira
     * ROLE_ADMINISTRADOR. Acentos e espaços são normalizados para que a
     * descrição cadastrada no banco possa ser usada nas regras de acesso.
     */
    public static String toAuthority(UserRole userRole) {
        String description = userRole == null ? null : userRole.getDescription();
        if (description == null || description.isBlank()) {
            return null;
        }
        String withoutAccents = Normalizer.normalize(description, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return "ROLE_" + withoutAccents.trim()
                .toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9]+", "_");
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String authority = toAuthority(user.getUserRole());
        return authority == null
                ? List.of()
                : List.of(new SimpleGrantedAuthority(authority));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }
}
