package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.User;
import com.pontuo.api_pontuo.entity.UserRole;
import com.pontuo.api_pontuo.repository.UserRepository;
import com.pontuo.api_pontuo.repository.UserRoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
class UserServiceTest {

    private static final Long ROLE_ID = 1L;
    private static final String DEFAULT_ROLE_DESCRIPTION = "Estudante";

    @Mock
    private UserRepository repository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService service;

    private UserRole studentRole;
    private User newUser;

    @BeforeEach
    void setUp() {
        studentRole = new UserRole(DEFAULT_ROLE_DESCRIPTION);
        studentRole.setId(ROLE_ID);

        newUser = new User("diego", "diego@pontuo.com", LocalDate.of(2000, 5, 17), "senha-clara");
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("codifica a senha, associa a role e persiste o usuário")
        void deveCriarUsuarioComSenhaCodificadaERoleAssociada() {
            when(repository.existsByUsername("diego")).thenReturn(false);
            when(repository.existsByEmail("diego@pontuo.com")).thenReturn(false);
            when(userRoleRepository.findById(ROLE_ID)).thenReturn(Optional.of(studentRole));
            when(passwordEncoder.encode("senha-clara")).thenReturn("$2a$10$hash");
            when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            User created = service.create(newUser, ROLE_ID);

            ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
            verify(repository).save(saved.capture());

            assertThat(saved.getValue().getPassword()).isEqualTo("$2a$10$hash");
            assertThat(saved.getValue().getUserRole()).isSameAs(studentRole);
            assertThat(created.getUsername()).isEqualTo("diego");
            assertThat(created.getPassword())
                    .as("a senha em texto claro nunca deve sobreviver ao create")
                    .isNotEqualTo("senha-clara");
        }

        @Test
        @DisplayName("rejeita username já cadastrado")
        void deveRejeitarUsernameDuplicado() {
            when(repository.existsByUsername("diego")).thenReturn(true);

            assertThatThrownBy(() -> service.create(newUser, ROLE_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("username já cadastrado");

            verify(repository, never()).save(any());
            verifyNoInteractions(passwordEncoder);
        }

        @Test
        @DisplayName("rejeita email já cadastrado")
        void deveRejeitarEmailDuplicado() {
            when(repository.existsByUsername("diego")).thenReturn(false);
            when(repository.existsByEmail("diego@pontuo.com")).thenReturn(true);

            assertThatThrownBy(() -> service.create(newUser, ROLE_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("email já cadastrado");

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("falha quando a role informada não existe")
        void deveFalharQuandoRoleNaoExiste() {
            when(repository.existsByUsername("diego")).thenReturn(false);
            when(repository.existsByEmail("diego@pontuo.com")).thenReturn(false);
            when(userRoleRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.create(newUser, 99L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("UserRole não encontrada: id=99");

            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("register")
    class Register {

        @Test
        @DisplayName("usa sempre a role padrão de estudante, ignorando qualquer role da requisição")
        void deveUsarRolePadraoDeEstudante() {
            UserRole admin = new UserRole("Administrador");
            admin.setId(42L);
            newUser.setUserRole(admin);

            when(userRoleRepository.findByDescription(DEFAULT_ROLE_DESCRIPTION))
                    .thenReturn(Optional.of(studentRole));
            when(repository.existsByUsername("diego")).thenReturn(false);
            when(repository.existsByEmail("diego@pontuo.com")).thenReturn(false);
            when(userRoleRepository.findById(ROLE_ID)).thenReturn(Optional.of(studentRole));
            when(passwordEncoder.encode("senha-clara")).thenReturn("$2a$10$hash");
            when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            User registered = service.register(newUser);

            assertThat(registered.getUserRole()).isSameAs(studentRole);
            assertThat(registered.getUserRole().getDescription()).isEqualTo(DEFAULT_ROLE_DESCRIPTION);
        }

        @Test
        @DisplayName("falha quando a role padrão não está cadastrada")
        void deveFalharQuandoRolePadraoNaoExiste() {
            when(userRoleRepository.findByDescription(DEFAULT_ROLE_DESCRIPTION))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.register(newUser))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("UserRole padrão não encontrada");

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("rejeita username já cadastrado também no cadastro público")
        void deveRejeitarUsernameDuplicadoNoCadastroPublico() {
            when(userRoleRepository.findByDescription(DEFAULT_ROLE_DESCRIPTION))
                    .thenReturn(Optional.of(studentRole));
            when(repository.existsByUsername("diego")).thenReturn(true);

            assertThatThrownBy(() -> service.register(newUser))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("username já cadastrado");

            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        private User existing;

        @BeforeEach
        void setUpExisting() {
            existing = new User("diego", "diego@pontuo.com", LocalDate.of(2000, 5, 17),
                    "$2a$10$hash-antigo", studentRole);
            existing.setId(10L);
        }

        @Test
        @DisplayName("atualiza os dados e recodifica a senha quando uma nova é informada")
        void deveAtualizarDadosERecodificarSenha() {
            User updated = new User("diego2", "diego2@pontuo.com", LocalDate.of(1999, 1, 2), "nova-senha");

            when(repository.findById(10L)).thenReturn(Optional.of(existing));
            when(repository.existsByUsername("diego2")).thenReturn(false);
            when(repository.existsByEmail("diego2@pontuo.com")).thenReturn(false);
            when(userRoleRepository.findById(ROLE_ID)).thenReturn(Optional.of(studentRole));
            when(passwordEncoder.encode("nova-senha")).thenReturn("$2a$10$hash-novo");
            when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            User result = service.update(10L, updated, ROLE_ID);

            assertThat(result.getId()).isEqualTo(10L);
            assertThat(result.getUsername()).isEqualTo("diego2");
            assertThat(result.getEmail()).isEqualTo("diego2@pontuo.com");
            assertThat(result.getBirthDate()).isEqualTo(LocalDate.of(1999, 1, 2));
            assertThat(result.getPassword()).isEqualTo("$2a$10$hash-novo");
        }

        @Test
        @DisplayName("mantém a senha atual quando o campo vem vazio")
        void deveManterSenhaQuandoCampoVazio() {
            User updated = new User("diego", "diego@pontuo.com", LocalDate.of(2000, 5, 17), "   ");

            when(repository.findById(10L)).thenReturn(Optional.of(existing));
            when(userRoleRepository.findById(ROLE_ID)).thenReturn(Optional.of(studentRole));
            when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            User result = service.update(10L, updated, ROLE_ID);

            assertThat(result.getPassword()).isEqualTo("$2a$10$hash-antigo");
            verify(passwordEncoder, never()).encode(anyString());
        }

        @Test
        @DisplayName("não acusa duplicidade quando o usuário mantém o próprio username e email")
        void naoDeveAcusarDuplicidadeComOsProprios() {
            User updated = new User("diego", "diego@pontuo.com", LocalDate.of(2000, 5, 17), null);

            when(repository.findById(10L)).thenReturn(Optional.of(existing));
            when(userRoleRepository.findById(ROLE_ID)).thenReturn(Optional.of(studentRole));
            when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            User result = service.update(10L, updated, ROLE_ID);

            verify(repository, never()).existsByUsername(anyString());
            verify(repository, never()).existsByEmail(anyString());
            assertThat(result.getPassword())
                    .as("senha nula no update mantém o hash atual")
                    .isEqualTo("$2a$10$hash-antigo");
        }

        @Test
        @DisplayName("rejeita email já usado por outro usuário")
        void deveRejeitarEmailDeOutroUsuario() {
            User updated = new User("diego", "outro@pontuo.com", LocalDate.of(2000, 5, 17), null);

            when(repository.findById(10L)).thenReturn(Optional.of(existing));
            when(repository.existsByEmail("outro@pontuo.com")).thenReturn(true);

            assertThatThrownBy(() -> service.update(10L, updated, ROLE_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("email já cadastrado");

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("falha quando a role informada não existe")
        void deveFalharQuandoRoleNaoExiste() {
            User updated = new User("diego", "diego@pontuo.com", LocalDate.of(2000, 5, 17), null);

            when(repository.findById(10L)).thenReturn(Optional.of(existing));
            when(userRoleRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.update(10L, updated, 99L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("UserRole não encontrada: id=99");

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("rejeita username já usado por outro usuário")
        void deveRejeitarUsernameDeOutroUsuario() {
            User updated = new User("outro", "diego@pontuo.com", LocalDate.of(2000, 5, 17), null);

            when(repository.findById(10L)).thenReturn(Optional.of(existing));
            when(repository.existsByUsername("outro")).thenReturn(true);

            assertThatThrownBy(() -> service.update(10L, updated, ROLE_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("username já cadastrado");

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("falha quando o usuário não existe")
        void deveFalharQuandoUsuarioNaoExiste() {
            when(repository.findById(404L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.update(404L, newUser, ROLE_ID))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("User não encontrado: id=404");

            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("remove o usuário existente")
        void deveRemoverUsuarioExistente() {
            when(repository.existsById(10L)).thenReturn(true);

            service.delete(10L);

            verify(repository).deleteById(10L);
        }

        @Test
        @DisplayName("falha quando o usuário não existe")
        void deveFalharQuandoUsuarioNaoExiste() {
            when(repository.existsById(404L)).thenReturn(false);

            assertThatThrownBy(() -> service.delete(404L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("User não encontrado: id=404");

            verify(repository, never()).deleteById(404L);
        }
    }

    @Nested
    @DisplayName("consultas")
    class Consultas {

        @Test
        @DisplayName("findByUserRoleId retorna os usuários da role")
        void deveListarUsuariosDaRole() {
            when(userRoleRepository.existsById(ROLE_ID)).thenReturn(true);
            when(repository.findByUserRoleId(ROLE_ID)).thenReturn(List.of(newUser));

            assertThat(service.findByUserRoleId(ROLE_ID)).containsExactly(newUser);
        }

        @Test
        @DisplayName("findByUserRoleId falha quando a role não existe")
        void deveFalharQuandoRoleNaoExiste() {
            when(userRoleRepository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> service.findByUserRoleId(99L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("UserRole não encontrada: id=99");

            verify(repository, never()).findByUserRoleId(any());
        }

        @Test
        @DisplayName("findByUsername delega ao repositório")
        void deveBuscarPorUsername() {
            when(repository.findByUsername("diego")).thenReturn(Optional.of(newUser));

            assertThat(service.findByUsername("diego")).contains(newUser);
        }
    }
}
