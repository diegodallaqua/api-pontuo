package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.KnowledgeArea;
import com.pontuo.api_pontuo.repository.KnowledgeAreaRepository;
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
@DisplayName("KnowledgeAreaService")
class KnowledgeAreaServiceTest {

    @Mock
    private KnowledgeAreaRepository repository;

    @InjectMocks
    private KnowledgeAreaService service;

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("atualiza a descrição da área existente")
        void deveAtualizarDescricao() {
            KnowledgeArea existente = new KnowledgeArea("Ciências Humanas");
            existente.setId(1L);
            when(repository.findById(1L)).thenReturn(Optional.of(existente));
            when(repository.save(any(KnowledgeArea.class))).thenAnswer(invocation -> invocation.getArgument(0));

            KnowledgeArea resultado = service.update(1L, new KnowledgeArea("Ciências Humanas e Sociais"));

            assertAll(
                    () -> assertEquals(1L, resultado.getId()),
                    () -> assertEquals("Ciências Humanas e Sociais", resultado.getDescription()));
        }

        @Test
        @DisplayName("falha quando a área não existe")
        void deveFalharQuandoAreaNaoExiste() {
            when(repository.findById(404L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.update(404L, new KnowledgeArea("Matemática")));

            assertEquals("KnowledgeArea não encontrada: id=404", ex.getMessage());
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("remove a área existente")
        void deveRemoverAreaExistente() {
            when(repository.existsById(1L)).thenReturn(true);

            service.delete(1L);

            verify(repository).deleteById(1L);
        }

        @Test
        @DisplayName("falha quando a área não existe")
        void deveFalharQuandoAreaNaoExiste() {
            when(repository.existsById(404L)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.delete(404L));

            assertEquals("KnowledgeArea não encontrada: id=404", ex.getMessage());
            verify(repository, never()).deleteById(any());
        }
    }
}
