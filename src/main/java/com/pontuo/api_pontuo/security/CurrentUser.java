package com.pontuo.api_pontuo.security;

import com.pontuo.api_pontuo.entity.User;
import com.pontuo.api_pontuo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Usuário dono do token da requisição. Serve às regras de propriedade — o
 * estudante só enxerga e altera os próprios simulados e o próprio desempenho —,
 * que a proteção por rota do SecurityConfig não consegue expressar.
 */
@Component
public class CurrentUser {

    private final UserRepository userRepository;

    public CurrentUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** O subject do token é o username (ver JwtService). */
    public User get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AccessDeniedException("Acesso negado: requisição sem usuário autenticado.");
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User não encontrado: username=" + username));
    }

    public boolean isAdministrador() {
        return Roles.isAdministrador();
    }

    /** O administrador acessa qualquer registro; o estudante, só os dele. */
    public void checkAccess(User owner) {
        if (isAdministrador()) {
            return;
        }
        if (owner == null || !owner.getId().equals(get().getId())) {
            throw new AccessDeniedException("Acesso negado: o registro pertence a outro usuário.");
        }
    }
}
