package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.AnswerOption;
import com.pontuo.api_pontuo.entity.Question;
import com.pontuo.api_pontuo.repository.AnswerOptionRepository;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnswerOptionService")
class AnswerOptionServiceTest {

    private static final Long QUESTAO_ID = 10L;
    private static final Long OUTRA_QUESTAO_ID = 20L;

    @Mock
    private AnswerOptionRepository repository;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private AnswerOptionService service;

    private Question questao;
    private Question outraQuestao;

    @BeforeEach
    void setUp() {
        questao = new Question("Enunciado", null, (short) 2);
        questao.setId(QUESTAO_ID);
        outraQuestao = new Question("Outro enunciado", null, (short) 1);
        outraQuestao.setId(OUTRA_QUESTAO_ID);
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("associa a alternativa incorreta à questão sem checar a correta")
        void deveCriarAlternativaIncorreta() {
            when(questionRepository.findById(QUESTAO_ID)).thenReturn(Optional.of(questao));
            when(repository.save(any(AnswerOption.class))).thenAnswer(invocation -> invocation.getArgument(0));

            AnswerOption criada = service.create(new AnswerOption("A", "Texto", false), QUESTAO_ID);

            assertSame(questao, criada.getQuestion());
            verify(repository, never()).existsByQuestionIdAndRightAnswerTrue(any());
        }

        @Test
        @DisplayName("aceita a primeira alternativa correta da questão")
        void deveCriarPrimeiraAlternativaCorreta() {
            when(repository.existsByQuestionIdAndRightAnswerTrue(QUESTAO_ID)).thenReturn(false);
            when(questionRepository.findById(QUESTAO_ID)).thenReturn(Optional.of(questao));
            when(repository.save(any(AnswerOption.class))).thenAnswer(invocation -> invocation.getArgument(0));

            AnswerOption criada = service.create(new AnswerOption("A", "Texto", true), QUESTAO_ID);

            assertTrue(criada.isRightAnswer());
        }

        @Test
        @DisplayName("rejeita letra repetida na mesma questão")
        void deveRejeitarLetraRepetida() {
            when(repository.existsByQuestionIdAndLetter(QUESTAO_ID, "A")).thenReturn(true);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.create(new AnswerOption("A", "Texto", false), QUESTAO_ID));

            assertEquals("letra já usada nesta questão: questionId=10, letter=A", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("rejeita segunda alternativa correta")
        void deveRejeitarSegundaAlternativaCorreta() {
            when(repository.existsByQuestionIdAndRightAnswerTrue(QUESTAO_ID)).thenReturn(true);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.create(new AnswerOption("B", "Texto", true), QUESTAO_ID));

            assertEquals("questão já tem alternativa correta: questionId=10", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("falha quando a questão não existe")
        void deveFalharQuandoQuestaoNaoExiste() {
            when(questionRepository.findById(QUESTAO_ID)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.create(new AnswerOption("A", "Texto", false), QUESTAO_ID));

            assertEquals("Question não encontrada: id=10", ex.getMessage());
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        private AnswerOption incorreta;
        private AnswerOption correta;

        @BeforeEach
        void setUpExistentes() {
            incorreta = new AnswerOption("A", "Texto antigo", false, questao);
            incorreta.setId(1L);
            correta = new AnswerOption("B", "Texto antigo", true, questao);
            correta.setId(2L);
        }

        private void salvarDevolvendoOArgumento() {
            when(repository.save(any(AnswerOption.class))).thenAnswer(invocation -> invocation.getArgument(0));
        }

        @Test
        @DisplayName("mantém a letra na mesma questão sem acusar colisão consigo mesma")
        void deveManterLetraSemRevalidar() {
            when(repository.findById(1L)).thenReturn(Optional.of(incorreta));
            when(questionRepository.findById(QUESTAO_ID)).thenReturn(Optional.of(questao));
            salvarDevolvendoOArgumento();

            AnswerOption resultado = service.update(1L, new AnswerOption("A", "Texto novo", false), QUESTAO_ID);

            assertEquals("Texto novo", resultado.getText());
            verify(repository, never()).existsByQuestionIdAndLetter(any(), anyString());
        }

        @Test
        @DisplayName("a alternativa correta continua correta sem colidir consigo mesma")
        void deveManterPropriaCorretaSemRevalidar() {
            when(repository.findById(2L)).thenReturn(Optional.of(correta));
            when(questionRepository.findById(QUESTAO_ID)).thenReturn(Optional.of(questao));
            salvarDevolvendoOArgumento();

            AnswerOption resultado = service.update(2L, new AnswerOption("B", "Texto novo", true), QUESTAO_ID);

            assertAll(
                    () -> assertTrue(resultado.isRightAnswer()),
                    () -> assertEquals("Texto novo", resultado.getText()));
            verify(repository, never()).existsByQuestionIdAndRightAnswerTrue(any());
        }

        @Test
        @DisplayName("rejeita trocar para uma letra já usada na questão")
        void deveRejeitarTrocaParaLetraUsada() {
            when(repository.findById(1L)).thenReturn(Optional.of(incorreta));
            when(repository.existsByQuestionIdAndLetter(QUESTAO_ID, "B")).thenReturn(true);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.update(1L, new AnswerOption("B", "Texto", false), QUESTAO_ID));

            assertEquals("letra já usada nesta questão: questionId=10, letter=B", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("rejeita marcar como correta quando a questão já tem outra correta")
        void deveRejeitarNovaCorreta() {
            when(repository.findById(1L)).thenReturn(Optional.of(incorreta));
            when(repository.existsByQuestionIdAndRightAnswerTrue(QUESTAO_ID)).thenReturn(true);

            assertThrows(IllegalArgumentException.class,
                    () -> service.update(1L, new AnswerOption("A", "Texto", true), QUESTAO_ID));

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("rejeita mover a correta para questão que já tem correta")
        void deveRejeitarMoverCorretaParaQuestaoComCorreta() {
            when(repository.findById(2L)).thenReturn(Optional.of(correta));
            when(repository.existsByQuestionIdAndLetter(OUTRA_QUESTAO_ID, "B")).thenReturn(false);
            when(repository.existsByQuestionIdAndRightAnswerTrue(OUTRA_QUESTAO_ID)).thenReturn(true);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.update(2L, new AnswerOption("B", "Texto", true), OUTRA_QUESTAO_ID));

            assertEquals("questão já tem alternativa correta: questionId=20", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("falha quando a alternativa não existe")
        void deveFalharQuandoAlternativaNaoExiste() {
            when(repository.findById(404L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.update(404L, new AnswerOption("A", "Texto", false), QUESTAO_ID));

            assertEquals("AnswerOption não encontrada: id=404", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("delete e consultas")
    class DeleteEConsultas {

        @Test
        @DisplayName("delete falha quando a alternativa não existe")
        void deveFalharAoRemoverInexistente() {
            when(repository.existsById(404L)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.delete(404L));

            assertEquals("AnswerOption não encontrada: id=404", ex.getMessage());
            verify(repository, never()).deleteById(any());
        }

        @Test
        @DisplayName("delete remove a alternativa existente")
        void deveRemoverExistente() {
            when(repository.existsById(1L)).thenReturn(true);

            service.delete(1L);

            verify(repository).deleteById(1L);
        }

        @Test
        @DisplayName("findByQuestionId falha quando a questão não existe")
        void deveFalharAoListarPorQuestaoInexistente() {
            when(questionRepository.existsById(QUESTAO_ID)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.findByQuestionId(QUESTAO_ID));

            assertEquals("Question não encontrada: id=10", ex.getMessage());
            verify(repository, never()).findByQuestionId(any());
        }
    }
}
