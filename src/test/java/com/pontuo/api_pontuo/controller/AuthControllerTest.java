package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.security.JwtService;
import com.pontuo.api_pontuo.security.TokenRevocationService;
import com.pontuo.api_pontuo.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController")
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenRevocationService revocationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController controller;

    private Jwt jwt;

    @BeforeEach
    void autenticarMaria() {
        jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .subject("maria")
                .jti("jti-1")
                .expiresAt(Instant.now().plus(Duration.ofHours(1)))
                .build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
    }

    @AfterEach
    void limparContexto() {
        // O SecurityContextHolder é estático: se um teste falhar antes do logout,
        // a autenticação não pode vazar para o próximo.
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("logout revoga o token e limpa o contexto de segurança")
    void deveRevogarTokenELimparContexto() {
        controller.logout(jwt);

        verify(revocationService).revoke(jwt);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("/me falha quando o usuário do token não existe mais")
    void deveFalharQuandoUsuarioDoTokenNaoExiste() {
        when(userService.findByUsername("maria")).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> controller.me(jwt));

        assertEquals("User não encontrado: username=maria", ex.getMessage());
    }
}
