package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.UserRole;
import com.pontuo.api_pontuo.repository.UserRoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRoleService")
class UserRoleServiceTest {

    @Mock
    private UserRoleRepository repository;

    @InjectMocks
    private UserRoleService service;

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("atualiza a descrição do perfil existente")
        void deveAtualizarDescricao() {
            UserRole existente = new UserRole("Estudante");
            existente.setId(2L);
            when(repository.findById(2L)).thenReturn(Optional.of(existente));
            when(repository.save(any(UserRole.class))).thenAnswer(invocation -> invocation.getArgument(0));

            UserRole resultado = service.update(2L, new UserRole("Aluno"));

            assertAll(
                    () -> assertEquals(2L, resultado.getId()),
                    () -> assertEquals("Aluno", resultado.getDescription()));
        }

        @Test
        @DisplayName("falha quando o perfil não existe")
        void deveFalharQuandoPerfilNaoExiste() {
            when(repository.findById(404L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.update(404L, new UserRole("Aluno")));

            assertEquals("UserRole não encontrada: id=404", ex.getMessage());
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("remove o perfil existente")
        void deveRemoverPerfilExistente() {
            when(repository.existsById(2L)).thenReturn(true);

            service.delete(2L);

            verify(repository).deleteById(2L);
        }

        @Test
        @DisplayName("falha quando o perfil não existe")
        void deveFalharQuandoPerfilNaoExiste() {
            when(repository.existsById(404L)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.delete(404L));

            assertEquals("UserRole não encontrada: id=404", ex.getMessage());
            verify(repository, never()).deleteById(any());
        }
    }
}
