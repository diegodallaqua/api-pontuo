package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.Subject;
import com.pontuo.api_pontuo.entity.Topic;
import com.pontuo.api_pontuo.repository.SubjectRepository;
import com.pontuo.api_pontuo.repository.TopicRepository;
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
@DisplayName("TopicService")
class TopicServiceTest {

    @Mock
    private TopicRepository repository;

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private TopicService service;

    private Subject biologia;

    @BeforeEach
    void setUp() {
        biologia = new Subject("Biologia");
        biologia.setId(5L);
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("associa o tópico à matéria")
        void deveAssociarMateria() {
            when(subjectRepository.findById(5L)).thenReturn(Optional.of(biologia));
            when(repository.save(any(Topic.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Topic criado = service.create(new Topic("Genética"), 5L);

            assertSame(biologia, criado.getSubject());
        }

        @Test
        @DisplayName("falha quando a matéria não existe")
        void deveFalharQuandoMateriaNaoExiste() {
            when(subjectRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.create(new Topic("Genética"), 99L));

            assertEquals("Subject não encontrada: id=99", ex.getMessage());
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        private Topic existente;

        @BeforeEach
        void setUpExistente() {
            existente = new Topic("Genética", biologia);
            existente.setId(34L);
        }

        @Test
        @DisplayName("atualiza descrição e matéria do tópico existente")
        void deveAtualizarCampos() {
            Subject fisica = new Subject("Física");
            fisica.setId(6L);
            when(repository.findById(34L)).thenReturn(Optional.of(existente));
            when(subjectRepository.findById(6L)).thenReturn(Optional.of(fisica));
            when(repository.save(any(Topic.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Topic resultado = service.update(34L, new Topic("Óptica"), 6L);

            assertAll(
                    () -> assertEquals(34L, resultado.getId()),
                    () -> assertEquals("Óptica", resultado.getDescription()),
                    () -> assertSame(fisica, resultado.getSubject()));
        }

        @Test
        @DisplayName("falha quando o tópico não existe")
        void deveFalharQuandoTopicoNaoExiste() {
            when(repository.findById(404L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.update(404L, new Topic("Óptica"), 6L));

            assertEquals("Topic não encontrada: id=404", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("falha quando a nova matéria não existe")
        void deveFalharQuandoMateriaNaoExiste() {
            when(repository.findById(34L)).thenReturn(Optional.of(existente));
            when(subjectRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.update(34L, new Topic("Óptica"), 99L));

            assertEquals("Subject não encontrada: id=99", ex.getMessage());
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete e consultas")
    class DeleteEConsultas {

        @Test
        @DisplayName("delete remove o tópico existente")
        void deveRemoverExistente() {
            when(repository.existsById(34L)).thenReturn(true);

            service.delete(34L);

            verify(repository).deleteById(34L);
        }

        @Test
        @DisplayName("delete falha quando o tópico não existe")
        void deveFalharAoRemoverInexistente() {
            when(repository.existsById(404L)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.delete(404L));

            assertEquals("Topic não encontrada: id=404", ex.getMessage());
            verify(repository, never()).deleteById(any());
        }

        @Test
        @DisplayName("findBySubjectId falha quando a matéria não existe")
        void deveFalharAoListarPorMateriaInexistente() {
            when(subjectRepository.existsById(99L)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.findBySubjectId(99L));

            assertEquals("Subject não encontrada: id=99", ex.getMessage());
            verify(repository, never()).findBySubjectId(any());
        }
    }
}
