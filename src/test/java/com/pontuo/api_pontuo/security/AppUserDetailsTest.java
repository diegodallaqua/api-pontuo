package com.pontuo.api_pontuo.security;

import com.pontuo.api_pontuo.entity.User;
import com.pontuo.api_pontuo.entity.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("AppUserDetails")
class AppUserDetailsTest {

    @Nested
    @DisplayName("toAuthority")
    class ToAuthority {

        @Test
        @DisplayName("Administrador vira ROLE_ADMINISTRADOR, o nome usado em hasRole no SecurityConfig")
        void deveGerarAuthorityUsadaNasRegrasDeAcesso() {
            assertEquals("ROLE_ADMINISTRADOR", AppUserDetails.toAuthority(new UserRole("Administrador")));
        }

        @ParameterizedTest(name = "\"{0}\" vira {1}")
        @DisplayName("remove acentos, espaços nas pontas e troca separadores por _")
        @CsvSource(delimiter = '|', value = {
                "Estudante              | ROLE_ESTUDANTE",
                "Coordenação Pedagógica | ROLE_COORDENACAO_PEDAGOGICA",
                "'  estudante '         | ROLE_ESTUDANTE"
        })
        void deveNormalizarDescricao(String descricao, String esperado) {
            assertEquals(esperado, AppUserDetails.toAuthority(new UserRole(descricao)));
        }

        @ParameterizedTest(name = "descrição [{0}]")
        @DisplayName("retorna null quando a descrição é nula ou em branco")
        @NullAndEmptySource
        @ValueSource(strings = "   ")
        void deveRetornarNullQuandoDescricaoAusente(String descricao) {
            assertNull(AppUserDetails.toAuthority(new UserRole(descricao)));
        }

        @Test
        @DisplayName("retorna null quando o usuário não tem role")
        void deveRetornarNullQuandoRoleNula() {
            assertNull(AppUserDetails.toAuthority(null));
        }
    }

    @Nested
    @DisplayName("getAuthorities")
    class GetAuthorities {

        @Test
        @DisplayName("expõe a authority da role do usuário")
        void deveExporAuthorityDaRole() {
            AppUserDetails details = new AppUserDetails(usuarioCom(new UserRole("Estudante")));

            assertEquals(List.of("ROLE_ESTUDANTE"), authorities(details));
        }

        @Test
        @DisplayName("usuário sem perfil não recebe nenhuma authority")
        void naoDeveConcederAuthoritySemRole() {
            AppUserDetails details = new AppUserDetails(usuarioCom(null));

            assertTrue(details.getAuthorities().isEmpty());
        }
    }

    private static User usuarioCom(UserRole role) {
        return new User("maria", "maria@pontuo.com", LocalDate.of(2001, 3, 9), "$2a$10$hash", role);
    }

    private static List<String> authorities(AppUserDetails details) {
        return details.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }
}
