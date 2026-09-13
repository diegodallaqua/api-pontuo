package com.pontuo.api_pontuo.security;

import com.pontuo.api_pontuo.entity.User;
import com.pontuo.api_pontuo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppUserDetailsService")
class AppUserDetailsServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private AppUserDetailsService service;

    private User maria;

    @BeforeEach
    void setUp() {
        maria = new User("maria", "maria@pontuo.com", LocalDate.of(2001, 3, 9), "$2a$10$hash");
    }

    @Test
    @DisplayName("encontra pelo username sem consultar o email")
    void deveCarregarPorUsernameSemConsultarEmail() {
        when(repository.findByUsername("maria")).thenReturn(Optional.of(maria));

        UserDetails details = service.loadUserByUsername("maria");

        assertAll(
                () -> assertEquals("maria", details.getUsername()),
                () -> assertSame(maria, ((AppUserDetails) details).user()));
        verify(repository, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("aceita o email quando nenhum username corresponde")
    void deveCarregarPorEmailQuandoUsernameNaoExiste() {
        when(repository.findByUsername("maria@pontuo.com")).thenReturn(Optional.empty());
        when(repository.findByEmail("maria@pontuo.com")).thenReturn(Optional.of(maria));

        UserDetails details = service.loadUserByUsername("maria@pontuo.com");

        assertSame(maria, ((AppUserDetails) details).user());
    }

    @Test
    @DisplayName("falha com mensagem genérica, sem revelar se a conta existe")
    void deveFalharComMensagemGenericaQuandoLoginNaoExiste() {
        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("ninguem"));

        assertEquals("Credenciais inválidas.", ex.getMessage());
    }
}
