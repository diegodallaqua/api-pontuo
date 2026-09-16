package com.pontuo.api_pontuo.security;

import com.pontuo.api_pontuo.entity.User;
import com.pontuo.api_pontuo.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Estratégia de carga do usuário no login (<b>Strategy</b>): o
 * {@code DaoAuthenticationProvider} depende apenas da interface
 * {@code UserDetailsService} e chama esta implementação, que busca no banco por
 * username ou email. Uma origem diferente de usuários entraria como outra
 * implementação, sem mudar o fluxo de autenticação.
 */
@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    public AppUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = repository.findByUsername(login)
                .or(() -> repository.findByEmail(login))
                .orElseThrow(() -> new UsernameNotFoundException("Credenciais inválidas."));
        return new AppUserDetails(user);
    }
}
