package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.State;
import com.pontuo.api_pontuo.repository.StateRepository;
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
@DisplayName("StateService")
class StateServiceTest {

    @Mock
    private StateRepository repository;

    @InjectMocks
    private StateService service;

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("atualiza o nome do estado existente")
        void deveAtualizarNome() {
            State existente = new State("Acre");
            existente.setId(1L);
            when(repository.findById(1L)).thenReturn(Optional.of(existente));
            when(repository.save(any(State.class))).thenAnswer(invocation -> invocation.getArgument(0));

            State resultado = service.update(1L, new State("Alagoas"));

            assertAll(
                    () -> assertEquals(1L, resultado.getId()),
                    () -> assertEquals("Alagoas", resultado.getName()));
        }

        @Test
        @DisplayName("falha quando o estado não existe")
        void deveFalharQuandoEstadoNaoExiste() {
            when(repository.findById(404L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.update(404L, new State("Alagoas")));

            assertEquals("State não encontrada: id=404", ex.getMessage());
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("remove o estado existente")
        void deveRemoverEstadoExistente() {
            when(repository.existsById(1L)).thenReturn(true);

            service.delete(1L);

            verify(repository).deleteById(1L);
        }

        @Test
        @DisplayName("falha quando o estado não existe")
        void deveFalharQuandoEstadoNaoExiste() {
            when(repository.existsById(404L)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.delete(404L));

            assertEquals("State não encontrada: id=404", ex.getMessage());
            verify(repository, never()).deleteById(any());
        }
    }
}
