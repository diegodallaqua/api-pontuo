package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.KnowledgeArea;
import com.pontuo.api_pontuo.entity.Subject;
import com.pontuo.api_pontuo.repository.KnowledgeAreaRepository;
import com.pontuo.api_pontuo.repository.SubjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubjectService")
class SubjectServiceTest {

    @Mock
    private SubjectRepository repository;

    @Mock
    private KnowledgeAreaRepository knowledgeAreaRepository;

    @InjectMocks
    private SubjectService service;

    private KnowledgeArea natureza;

    @BeforeEach
    void setUp() {
        natureza = new KnowledgeArea("Ciências da Natureza");
        natureza.setId(2L);
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("associa a matéria à área de conhecimento")
        void deveAssociarArea() {
            when(knowledgeAreaRepository.findById(2L)).thenReturn(Optional.of(natureza));
            when(repository.save(any(Subject.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Subject criada = service.create(new Subject("Biologia"), 2L);

            assertSame(natureza, criada.getKnowledgeArea());
        }

        @Test
        @DisplayName("falha quando a área não existe")
        void deveFalharQuandoAreaNaoExiste() {
            when(knowledgeAreaRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.create(new Subject("Biologia"), 99L));

            assertEquals("KnowledgeArea não encontrada: id=99", ex.getMessage());
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        private Subject existente;

        @BeforeEach
        void setUpExistente() {
            existente = new Subject("Biologia", natureza);
            existente.setId(5L);
        }

        @Test
        @DisplayName("atualiza descrição e área da matéria existente")
        void deveAtualizarCampos() {
            KnowledgeArea humanas = new KnowledgeArea("Ciências Humanas");
            humanas.setId(1L);
            when(repository.findById(5L)).thenReturn(Optional.of(existente));
            when(knowledgeAreaRepository.findById(1L)).thenReturn(Optional.of(humanas));
            when(repository.save(any(Subject.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Subject resultado = service.update(5L, new Subject("Geografia"), 1L);

            assertAll(
                    () -> assertEquals(5L, resultado.getId()),
                    () -> assertEquals("Geografia", resultado.getDescription()),
                    () -> assertSame(humanas, resultado.getKnowledgeArea()));
        }

        @Test
        @DisplayName("falha quando a matéria não existe")
        void deveFalharQuandoMateriaNaoExiste() {
            when(repository.findById(404L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.update(404L, new Subject("Geografia"), 1L));

            assertEquals("Subject não encontrada: id=404", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("falha quando a nova área não existe")
        void deveFalharQuandoAreaNaoExiste() {
            when(repository.findById(5L)).thenReturn(Optional.of(existente));
            when(knowledgeAreaRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.update(5L, new Subject("Geografia"), 99L));

            assertEquals("KnowledgeArea não encontrada: id=99", ex.getMessage());
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete e consultas")
    class DeleteEConsultas {

        @Test
        @DisplayName("delete remove a matéria existente")
        void deveRemoverExistente() {
            when(repository.existsById(5L)).thenReturn(true);

            service.delete(5L);

            verify(repository).deleteById(5L);
        }

        @Test
        @DisplayName("delete falha quando a matéria não existe")
        void deveFalharAoRemoverInexistente() {
            when(repository.existsById(404L)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.delete(404L));

            assertEquals("Subject não encontrada: id=404", ex.getMessage());
            verify(repository, never()).deleteById(any());
        }

        @Test
        @DisplayName("findByKnowledgeAreaId falha quando a área não existe")
        void deveFalharAoListarPorAreaInexistente() {
            when(knowledgeAreaRepository.existsById(99L)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.findByKnowledgeAreaId(99L));

            assertEquals("KnowledgeArea não encontrada: id=99", ex.getMessage());
            verify(repository, never()).findByKnowledgeAreaId(any());
        }
    }
}
