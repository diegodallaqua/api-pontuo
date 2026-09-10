package com.pontuo.api_pontuo.security;

import com.pontuo.api_pontuo.entity.User;
import com.pontuo.api_pontuo.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    public AppUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * Aceita username ou email como identificador de login. A mensagem de erro
     * é sempre genérica para não revelar se a conta existe.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = repository.findByUsername(login)
                .or(() -> repository.findByEmail(login))
                .orElseThrow(() -> new UsernameNotFoundException("Credenciais inválidas."));
        return new AppUserDetails(user);
    }
}
