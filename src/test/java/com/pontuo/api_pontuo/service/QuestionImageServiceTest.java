package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.Question;
import com.pontuo.api_pontuo.entity.QuestionImage;
import com.pontuo.api_pontuo.repository.QuestionImageRepository;
import com.pontuo.api_pontuo.repository.QuestionRepository;
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
@DisplayName("QuestionImageService")
class QuestionImageServiceTest {

    @Mock
    private QuestionImageRepository repository;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuestionImageService service;

    private Question questao;

    @BeforeEach
    void setUp() {
        questao = new Question("Enunciado com gráfico", null, (short) 2);
        questao.setId(10L);
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("associa a imagem à questão")
        void deveAssociarQuestao() {
            when(questionRepository.findById(10L)).thenReturn(Optional.of(questao));
            when(repository.save(any(QuestionImage.class))).thenAnswer(invocation -> invocation.getArgument(0));

            QuestionImage criada = service.create(new QuestionImage("https://cdn.pontuo.com/grafico.png", null), 10L);

            assertSame(questao, criada.getQuestion());
        }

        @Test
        @DisplayName("falha quando a questão não existe")
        void deveFalharQuandoQuestaoNaoExiste() {
            when(questionRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.create(new QuestionImage("https://cdn.pontuo.com/grafico.png", null), 99L));

            assertEquals("Question não encontrada: id=99", ex.getMessage());
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        private QuestionImage existente;

        @BeforeEach
        void setUpExistente() {
            existente = new QuestionImage("https://cdn.pontuo.com/antiga.png", "Legenda antiga", questao);
            existente.setId(3L);
        }

        @Test
        @DisplayName("atualiza url, legenda e questão da imagem existente")
        void deveAtualizarCampos() {
            Question outra = new Question("Outro enunciado", null, (short) 1);
            outra.setId(11L);
            when(repository.findById(3L)).thenReturn(Optional.of(existente));
            when(questionRepository.findById(11L)).thenReturn(Optional.of(outra));
            when(repository.save(any(QuestionImage.class))).thenAnswer(invocation -> invocation.getArgument(0));

            QuestionImage resultado = service.update(3L,
                    new QuestionImage("https://cdn.pontuo.com/nova.png", "Figura 1"), 11L);

            assertAll(
                    () -> assertEquals(3L, resultado.getId()),
                    () -> assertEquals("https://cdn.pontuo.com/nova.png", resultado.getUrl()),
                    () -> assertEquals("Figura 1", resultado.getSubtitle()),
                    () -> assertSame(outra, resultado.getQuestion()));
        }

        @Test
        @DisplayName("falha quando a imagem não existe")
        void deveFalharQuandoImagemNaoExiste() {
            when(repository.findById(404L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.update(404L, new QuestionImage("https://cdn.pontuo.com/nova.png", null), 10L));

            assertEquals("QuestionImage não encontrada: id=404", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("falha quando a nova questão não existe")
        void deveFalharQuandoQuestaoNaoExiste() {
            when(repository.findById(3L)).thenReturn(Optional.of(existente));
            when(questionRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.update(3L, new QuestionImage("https://cdn.pontuo.com/nova.png", null), 99L));

            assertEquals("Question não encontrada: id=99", ex.getMessage());
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete e consultas")
    class DeleteEConsultas {

        @Test
        @DisplayName("delete remove a imagem existente")
        void deveRemoverExistente() {
            when(repository.existsById(3L)).thenReturn(true);

            service.delete(3L);

            verify(repository).deleteById(3L);
        }

        @Test
        @DisplayName("delete falha quando a imagem não existe")
        void deveFalharAoRemoverInexistente() {
            when(repository.existsById(404L)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.delete(404L));

            assertEquals("QuestionImage não encontrada: id=404", ex.getMessage());
            verify(repository, never()).deleteById(any());
        }

        @Test
        @DisplayName("findByQuestionId falha quando a questão não existe")
        void deveFalharAoListarPorQuestaoInexistente() {
            when(questionRepository.existsById(99L)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.findByQuestionId(99L));

            assertEquals("Question não encontrada: id=99", ex.getMessage());
            verify(repository, never()).findByQuestionId(any());
        }
    }
}
