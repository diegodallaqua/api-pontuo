package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.AnswerOption;
import com.pontuo.api_pontuo.entity.MockExam;
import com.pontuo.api_pontuo.entity.MockExamHasQuestion;
import com.pontuo.api_pontuo.entity.Question;
import com.pontuo.api_pontuo.repository.AnswerOptionRepository;
import com.pontuo.api_pontuo.repository.MockExamHasQuestionRepository;
import com.pontuo.api_pontuo.repository.MockExamRepository;
import com.pontuo.api_pontuo.repository.QuestionRepository;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MockExamHasQuestionService")
class MockExamHasQuestionServiceTest {

    private static final Long SIMULADO_ID = 1L;
    private static final Long QUESTAO_ID = 2L;
    private static final Long ALTERNATIVA_ID = 3L;
    private static final Long ALTERNATIVA_DE_OUTRA_QUESTAO_ID = 4L;
    private static final Long OUTRA_QUESTAO_ID = 5L;
    private static final LocalDateTime RESPONDIDA_EM = LocalDateTime.of(2026, 9, 12, 14, 30);

    @Mock
    private MockExamHasQuestionRepository repository;

    @Mock
    private MockExamRepository mockExamRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerOptionRepository answerOptionRepository;

    @InjectMocks
    private MockExamHasQuestionService service;

    private MockExam simulado;
    private Question questao;
    private AnswerOption alternativa;
    private AnswerOption alternativaDeOutraQuestao;

    @BeforeEach
    void setUp() {
        simulado = new MockExam("Simulado ENEM", (short) 10, (short) 90);
        simulado.setId(SIMULADO_ID);

        questao = new Question("Enunciado", null, (short) 2);
        questao.setId(QUESTAO_ID);
        alternativa = new AnswerOption("A", "Alternativa da questão", true, questao);
        alternativa.setId(ALTERNATIVA_ID);

        Question outraQuestao = new Question("Outro enunciado", null, (short) 1);
        outraQuestao.setId(OUTRA_QUESTAO_ID);
        alternativaDeOutraQuestao = new AnswerOption("B", "Alternativa de outra questão", false, outraQuestao);
        alternativaDeOutraQuestao.setId(ALTERNATIVA_DE_OUTRA_QUESTAO_ID);
    }

    private void simuladoEQuestaoExistem() {
        when(mockExamRepository.findById(SIMULADO_ID)).thenReturn(Optional.of(simulado));
        when(questionRepository.findById(QUESTAO_ID)).thenReturn(Optional.of(questao));
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("vincula a questão já respondida ao simulado")
        void deveVincularQuestaoRespondida() {
            simuladoEQuestaoExistem();
            when(answerOptionRepository.findById(ALTERNATIVA_ID)).thenReturn(Optional.of(alternativa));

            service.create(SIMULADO_ID, QUESTAO_ID, ALTERNATIVA_ID, RESPONDIDA_EM);

            ArgumentCaptor<MockExamHasQuestion> salvo = ArgumentCaptor.forClass(MockExamHasQuestion.class);
            verify(repository).save(salvo.capture());
            assertAll(
                    () -> assertSame(simulado, salvo.getValue().getMockExam()),
                    () -> assertSame(questao, salvo.getValue().getQuestion()),
                    () -> assertSame(alternativa, salvo.getValue().getAnswerOption()),
                    () -> assertEquals(RESPONDIDA_EM, salvo.getValue().getAnsweredAt()));
        }

        @Test
        @DisplayName("vincula questão ainda sem resposta, sem consultar alternativas")
        void deveVincularQuestaoSemResposta() {
            simuladoEQuestaoExistem();

            service.create(SIMULADO_ID, QUESTAO_ID, null, null);

            ArgumentCaptor<MockExamHasQuestion> salvo = ArgumentCaptor.forClass(MockExamHasQuestion.class);
            verify(repository).save(salvo.capture());
            assertAll(
                    () -> assertNull(salvo.getValue().getAnswerOption()),
                    () -> assertNull(salvo.getValue().getAnsweredAt()));
            verifyNoInteractions(answerOptionRepository);
        }

        @Test
        @DisplayName("rejeita a mesma questão duas vezes no simulado")
        void deveRejeitarQuestaoJaVinculada() {
            when(repository.existsByMockExamIdAndQuestionId(SIMULADO_ID, QUESTAO_ID)).thenReturn(true);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.create(SIMULADO_ID, QUESTAO_ID, null, null));

            assertEquals("questão já vinculada ao simulado: mockExamId=1, questionId=2", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("exige answeredAt quando a alternativa é informada")
        void deveExigirDataDaResposta() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.create(SIMULADO_ID, QUESTAO_ID, ALTERNATIVA_ID, null));

            assertEquals("answeredAt é obrigatório quando answerOptionId é informado", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("rejeita answeredAt sem alternativa")
        void deveRejeitarDataSemAlternativa() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.create(SIMULADO_ID, QUESTAO_ID, null, RESPONDIDA_EM));

            assertEquals("answeredAt não pode ser informado sem answerOptionId", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("rejeita alternativa que pertence a outra questão")
        void deveRejeitarAlternativaDeOutraQuestao() {
            simuladoEQuestaoExistem();
            when(answerOptionRepository.findById(ALTERNATIVA_DE_OUTRA_QUESTAO_ID))
                    .thenReturn(Optional.of(alternativaDeOutraQuestao));

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.create(SIMULADO_ID, QUESTAO_ID, ALTERNATIVA_DE_OUTRA_QUESTAO_ID, RESPONDIDA_EM));

            assertEquals("answerOptionId=4 não pertence à questão: questionId=2", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("falha quando o simulado não existe")
        void deveFalharQuandoSimuladoNaoExiste() {
            when(mockExamRepository.findById(SIMULADO_ID)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.create(SIMULADO_ID, QUESTAO_ID, null, null));

            assertEquals("MockExam não encontrado: id=1", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("falha quando a questão não existe")
        void deveFalharQuandoQuestaoNaoExiste() {
            when(mockExamRepository.findById(SIMULADO_ID)).thenReturn(Optional.of(simulado));
            when(questionRepository.findById(QUESTAO_ID)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.create(SIMULADO_ID, QUESTAO_ID, null, null));

            assertEquals("Question não encontrada: id=2", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("falha quando a alternativa não existe")
        void deveFalharQuandoAlternativaNaoExiste() {
            simuladoEQuestaoExistem();
            when(answerOptionRepository.findById(ALTERNATIVA_ID)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.create(SIMULADO_ID, QUESTAO_ID, ALTERNATIVA_ID, RESPONDIDA_EM));

            assertEquals("AnswerOption não encontrada: id=3", ex.getMessage());
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        private MockExamHasQuestion vinculo;

        @BeforeEach
        void setUpVinculo() {
            vinculo = new MockExamHasQuestion(simulado, questao);
            vinculo.setId(10L);
        }

        @Test
        @DisplayName("registra a resposta sem acusar duplicidade do próprio par")
        void deveResponderSemRevalidarOProprioPar() {
            when(repository.findById(10L)).thenReturn(Optional.of(vinculo));
            simuladoEQuestaoExistem();
            when(answerOptionRepository.findById(ALTERNATIVA_ID)).thenReturn(Optional.of(alternativa));
            when(repository.save(any(MockExamHasQuestion.class))).thenAnswer(invocation -> invocation.getArgument(0));

            MockExamHasQuestion resultado = service.update(10L, SIMULADO_ID, QUESTAO_ID, ALTERNATIVA_ID, RESPONDIDA_EM);

            assertAll(
                    () -> assertSame(alternativa, resultado.getAnswerOption()),
                    () -> assertEquals(RESPONDIDA_EM, resultado.getAnsweredAt()));
            verify(repository, never()).existsByMockExamIdAndQuestionId(any(), any());
        }

        @Test
        @DisplayName("rejeita trocar para uma questão que já está no simulado")
        void deveRejeitarTrocaParaParJaVinculado() {
            when(repository.findById(10L)).thenReturn(Optional.of(vinculo));
            when(repository.existsByMockExamIdAndQuestionId(SIMULADO_ID, OUTRA_QUESTAO_ID)).thenReturn(true);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.update(10L, SIMULADO_ID, OUTRA_QUESTAO_ID, null, null));

            assertEquals("questão já vinculada ao simulado: mockExamId=1, questionId=5", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("aplica a mesma regra de resposta do create")
        void deveAplicarRegraDeResposta() {
            when(repository.findById(10L)).thenReturn(Optional.of(vinculo));

            assertThrows(IllegalArgumentException.class,
                    () -> service.update(10L, SIMULADO_ID, QUESTAO_ID, ALTERNATIVA_ID, null));

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("falha quando o vínculo não existe")
        void deveFalharQuandoVinculoNaoExiste() {
            when(repository.findById(404L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.update(404L, SIMULADO_ID, QUESTAO_ID, null, null));

            assertEquals("MockExamHasQuestion não encontrado: id=404", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("remove o vínculo existente")
        void deveRemoverVinculoExistente() {
            when(repository.existsById(10L)).thenReturn(true);

            service.delete(10L);

            verify(repository).deleteById(10L);
        }

        @Test
        @DisplayName("falha quando o vínculo não existe")
        void deveFalharQuandoVinculoNaoExiste() {
            when(repository.existsById(404L)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.delete(404L));

            assertEquals("MockExamHasQuestion não encontrado: id=404", ex.getMessage());
            verify(repository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("consultas")
    class Consultas {

        @Test
        @DisplayName("findByMockExamId falha quando o simulado não existe")
        void deveFalharAoListarPorSimuladoInexistente() {
            when(mockExamRepository.existsById(SIMULADO_ID)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.findByMockExamId(SIMULADO_ID));

            assertEquals("MockExam não encontrado: id=1", ex.getMessage());
            verify(repository, never()).findByMockExamId(any());
        }

        @Test
        @DisplayName("findByQuestionId falha quando a questão não existe")
        void deveFalharAoListarPorQuestaoInexistente() {
            when(questionRepository.existsById(QUESTAO_ID)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.findByQuestionId(QUESTAO_ID));

            assertEquals("Question não encontrada: id=2", ex.getMessage());
            verify(repository, never()).findByQuestionId(any());
        }
    }
}
