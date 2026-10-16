package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.AnswerOption;
import com.pontuo.api_pontuo.entity.MockExam;
import com.pontuo.api_pontuo.entity.MockExamHasQuestion;
import com.pontuo.api_pontuo.entity.Question;
import com.pontuo.api_pontuo.entity.User;
import com.pontuo.api_pontuo.repository.AnswerOptionRepository;
import com.pontuo.api_pontuo.repository.MockExamHasQuestionRepository;
import com.pontuo.api_pontuo.repository.MockExamRepository;
import com.pontuo.api_pontuo.repository.QuestionRepository;
import com.pontuo.api_pontuo.security.CurrentUser;
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
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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
    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 9, 12, 14, 0);

    @Mock
    private MockExamHasQuestionRepository repository;

    @Mock
    private MockExamRepository mockExamRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerOptionRepository answerOptionRepository;

    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private MockExamHasQuestionService service;

    private User aluno;
    private MockExam simulado;
    private Question questao;
    private AnswerOption alternativa;
    private AnswerOption alternativaDeOutraQuestao;

    @BeforeEach
    void setUp() {
        aluno = new User("maria", "maria@pontuo.com", LocalDate.of(2001, 3, 9), "$2a$10$hash");
        aluno.setId(7L);
        simulado = new MockExam("Simulado ENEM", (short) 10, (short) 90, aluno);
        simulado.setId(SIMULADO_ID);
        simulado.setStartedAt(INICIO);

        questao = new Question("Enunciado", null, (short) 2);
        questao.setId(QUESTAO_ID);
        alternativa = new AnswerOption("A", "Alternativa da questão", true, questao);
        alternativa.setId(ALTERNATIVA_ID);

        Question outraQuestao = new Question("Outro enunciado", null, (short) 1);
        outraQuestao.setId(OUTRA_QUESTAO_ID);
        alternativaDeOutraQuestao = new AnswerOption("B", "Alternativa de outra questão", false, outraQuestao);
        alternativaDeOutraQuestao.setId(ALTERNATIVA_DE_OUTRA_QUESTAO_ID);
    }

    private void simuladoExiste() {
        when(mockExamRepository.findById(SIMULADO_ID)).thenReturn(Optional.of(simulado));
    }

    private void simuladoEQuestaoExistem() {
        simuladoExiste();
        when(questionRepository.findById(QUESTAO_ID)).thenReturn(Optional.of(questao));
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("vincula a questão respondida e congela a correção")
        void deveVincularQuestaoRespondida() {
            simuladoEQuestaoExistem();
            when(answerOptionRepository.findById(ALTERNATIVA_ID)).thenReturn(Optional.of(alternativa));

            service.create(SIMULADO_ID, QUESTAO_ID, ALTERNATIVA_ID);

            ArgumentCaptor<MockExamHasQuestion> salvo = ArgumentCaptor.forClass(MockExamHasQuestion.class);
            verify(repository).save(salvo.capture());
            assertAll(
                    () -> assertSame(simulado, salvo.getValue().getMockExam()),
                    () -> assertSame(questao, salvo.getValue().getQuestion()),
                    () -> assertSame(alternativa, salvo.getValue().getAnswerOption()),
                    () -> assertNotNull(salvo.getValue().getAnsweredAt()),
                    () -> assertTrue(salvo.getValue().getIsCorrect()));
        }

        @Test
        @DisplayName("vincula questão sem resposta mesmo antes do início")
        void deveVincularQuestaoSemResposta() {
            simulado.setStartedAt(null);
            simuladoEQuestaoExistem();

            service.create(SIMULADO_ID, QUESTAO_ID, null);

            ArgumentCaptor<MockExamHasQuestion> salvo = ArgumentCaptor.forClass(MockExamHasQuestion.class);
            verify(repository).save(salvo.capture());
            assertAll(
                    () -> assertNull(salvo.getValue().getAnswerOption()),
                    () -> assertNull(salvo.getValue().getAnsweredAt()),
                    () -> assertNull(salvo.getValue().getIsCorrect()));
            verifyNoInteractions(answerOptionRepository);
        }

        @Test
        @DisplayName("recusa responder antes do início do simulado")
        void deveRecusarRespostaAntesDoInicio() {
            simulado.setStartedAt(null);
            simuladoEQuestaoExistem();

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.create(SIMULADO_ID, QUESTAO_ID, ALTERNATIVA_ID));

            assertEquals("simulado ainda não foi iniciado: mockExamId=1", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("rejeita a mesma questão duas vezes no simulado")
        void deveRejeitarQuestaoJaVinculada() {
            simuladoExiste();
            when(repository.existsByMockExamIdAndQuestionId(SIMULADO_ID, QUESTAO_ID)).thenReturn(true);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.create(SIMULADO_ID, QUESTAO_ID, null));

            assertEquals("questão já vinculada ao simulado: mockExamId=1, questionId=2", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("recusa vincular além de numQuestions")
        void deveRecusarAlemDoLimite() {
            simuladoExiste();
            when(repository.countByMockExamId(SIMULADO_ID)).thenReturn(10L);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.create(SIMULADO_ID, QUESTAO_ID, null));

            assertEquals("simulado já tem as 10 questões previstas em numQuestions: mockExamId=1", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("aceita a última questão dentro do limite")
        void deveAceitarUltimaQuestaoDoLimite() {
            simuladoEQuestaoExistem();
            when(repository.countByMockExamId(SIMULADO_ID)).thenReturn(9L);

            service.create(SIMULADO_ID, QUESTAO_ID, null);

            verify(repository).save(any(MockExamHasQuestion.class));
        }

        @Test
        @DisplayName("recusa vincular em simulado finalizado")
        void deveRecusarSimuladoFinalizado() {
            simulado.setFinishedAt(INICIO.plusMinutes(30));
            simuladoExiste();

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.create(SIMULADO_ID, QUESTAO_ID, null));

            assertEquals("simulado já finalizado não pode ser alterado: mockExamId=1", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("recusa vincular no simulado de outro usuário")
        void deveRecusarSimuladoDeOutro() {
            simuladoExiste();
            doThrow(new AccessDeniedException("negado")).when(currentUser).checkAccess(aluno);

            assertThrows(AccessDeniedException.class, () -> service.create(SIMULADO_ID, QUESTAO_ID, null));

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("rejeita alternativa que pertence a outra questão")
        void deveRejeitarAlternativaDeOutraQuestao() {
            simuladoEQuestaoExistem();
            when(answerOptionRepository.findById(ALTERNATIVA_DE_OUTRA_QUESTAO_ID))
                    .thenReturn(Optional.of(alternativaDeOutraQuestao));

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.create(SIMULADO_ID, QUESTAO_ID, ALTERNATIVA_DE_OUTRA_QUESTAO_ID));

            assertEquals("answerOptionId=4 não pertence à questão: questionId=2", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("falha quando o simulado não existe")
        void deveFalharQuandoSimuladoNaoExiste() {
            when(mockExamRepository.findById(SIMULADO_ID)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.create(SIMULADO_ID, QUESTAO_ID, null));

            assertEquals("MockExam não encontrado: id=1", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("falha quando a questão não existe")
        void deveFalharQuandoQuestaoNaoExiste() {
            simuladoExiste();
            when(questionRepository.findById(QUESTAO_ID)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.create(SIMULADO_ID, QUESTAO_ID, null));

            assertEquals("Question não encontrada: id=2", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("falha quando a alternativa não existe")
        void deveFalharQuandoAlternativaNaoExiste() {
            simuladoEQuestaoExistem();
            when(answerOptionRepository.findById(ALTERNATIVA_ID)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.create(SIMULADO_ID, QUESTAO_ID, ALTERNATIVA_ID));

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
            when(questionRepository.findById(QUESTAO_ID)).thenReturn(Optional.of(questao));
            when(answerOptionRepository.findById(ALTERNATIVA_ID)).thenReturn(Optional.of(alternativa));
            when(repository.save(any(MockExamHasQuestion.class))).thenAnswer(invocation -> invocation.getArgument(0));

            MockExamHasQuestion resultado = service.update(10L, SIMULADO_ID, QUESTAO_ID, ALTERNATIVA_ID);

            assertAll(
                    () -> assertSame(alternativa, resultado.getAnswerOption()),
                    () -> assertNotNull(resultado.getAnsweredAt()),
                    () -> assertTrue(resultado.getIsCorrect()));
            verify(repository, never()).existsByMockExamIdAndQuestionId(any(), any());
        }

        @Test
        @DisplayName("repetir a mesma alternativa mantém o horário da resposta")
        void deveManterHorarioAoRepetirAlternativa() {
            LocalDateTime respondidaEm = INICIO.plusMinutes(3);
            vinculo.answer(alternativa, respondidaEm);
            when(repository.findById(10L)).thenReturn(Optional.of(vinculo));
            when(questionRepository.findById(QUESTAO_ID)).thenReturn(Optional.of(questao));
            when(repository.save(any(MockExamHasQuestion.class))).thenAnswer(invocation -> invocation.getArgument(0));

            MockExamHasQuestion resultado = service.update(10L, SIMULADO_ID, QUESTAO_ID, ALTERNATIVA_ID);

            assertEquals(respondidaEm, resultado.getAnsweredAt());
            verifyNoInteractions(answerOptionRepository);
        }

        @Test
        @DisplayName("alternativa nula limpa a resposta")
        void deveLimparResposta() {
            vinculo.answer(alternativa, INICIO.plusMinutes(3));
            when(repository.findById(10L)).thenReturn(Optional.of(vinculo));
            when(questionRepository.findById(QUESTAO_ID)).thenReturn(Optional.of(questao));
            when(repository.save(any(MockExamHasQuestion.class))).thenAnswer(invocation -> invocation.getArgument(0));

            MockExamHasQuestion resultado = service.update(10L, SIMULADO_ID, QUESTAO_ID, null);

            assertAll(
                    () -> assertNull(resultado.getAnswerOption()),
                    () -> assertNull(resultado.getAnsweredAt()),
                    () -> assertNull(resultado.getIsCorrect()));
        }

        @Test
        @DisplayName("rejeita trocar para uma questão que já está no simulado")
        void deveRejeitarTrocaParaParJaVinculado() {
            when(repository.findById(10L)).thenReturn(Optional.of(vinculo));
            when(repository.existsByMockExamIdAndQuestionId(SIMULADO_ID, OUTRA_QUESTAO_ID)).thenReturn(true);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.update(10L, SIMULADO_ID, OUTRA_QUESTAO_ID, null));

            assertEquals("questão já vinculada ao simulado: mockExamId=1, questionId=5", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("recusa alterar resposta de simulado finalizado")
        void deveRecusarSimuladoFinalizado() {
            simulado.setFinishedAt(INICIO.plusMinutes(30));
            when(repository.findById(10L)).thenReturn(Optional.of(vinculo));

            assertThrows(IllegalArgumentException.class,
                    () -> service.update(10L, SIMULADO_ID, QUESTAO_ID, ALTERNATIVA_ID));

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("falha quando o vínculo não existe")
        void deveFalharQuandoVinculoNaoExiste() {
            when(repository.findById(404L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.update(404L, SIMULADO_ID, QUESTAO_ID, null));

            assertEquals("MockExamHasQuestion não encontrado: id=404", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        private MockExamHasQuestion vinculo;

        @BeforeEach
        void setUpVinculo() {
            vinculo = new MockExamHasQuestion(simulado, questao);
            vinculo.setId(10L);
        }

        @Test
        @DisplayName("remove o vínculo existente")
        void deveRemoverVinculoExistente() {
            when(repository.findById(10L)).thenReturn(Optional.of(vinculo));

            service.delete(10L);

            verify(repository).delete(vinculo);
        }

        @Test
        @DisplayName("recusa remover questão de simulado finalizado")
        void deveRecusarSimuladoFinalizado() {
            simulado.setFinishedAt(INICIO.plusMinutes(30));
            when(repository.findById(10L)).thenReturn(Optional.of(vinculo));

            assertThrows(IllegalArgumentException.class, () -> service.delete(10L));

            verify(repository, never()).delete(any());
        }

        @Test
        @DisplayName("falha quando o vínculo não existe")
        void deveFalharQuandoVinculoNaoExiste() {
            when(repository.findById(404L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.delete(404L));

            assertEquals("MockExamHasQuestion não encontrado: id=404", ex.getMessage());
            verify(repository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("consultas")
    class Consultas {

        @Test
        @DisplayName("findByMockExamId falha quando o simulado não existe")
        void deveFalharAoListarPorSimuladoInexistente() {
            when(mockExamRepository.findById(SIMULADO_ID)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.findByMockExamId(SIMULADO_ID));

            assertEquals("MockExam não encontrado: id=1", ex.getMessage());
            verify(repository, never()).findByMockExamId(any());
        }

        @Test
        @DisplayName("findByMockExamId recusa simulado de outro usuário")
        void deveRecusarListarSimuladoDeOutro() {
            simuladoExiste();
            doThrow(new AccessDeniedException("negado")).when(currentUser).checkAccess(aluno);

            assertThrows(AccessDeniedException.class, () -> service.findByMockExamId(SIMULADO_ID));

            verify(repository, never()).findByMockExamId(any());
        }

        @Test
        @DisplayName("findByQuestionId do estudante traz só os vínculos dos simulados dele")
        void estudanteDeveVerSoOsPropriosVinculos() {
            when(questionRepository.existsById(QUESTAO_ID)).thenReturn(true);
            when(currentUser.isAdministrador()).thenReturn(false);
            when(currentUser.get()).thenReturn(aluno);

            service.findByQuestionId(QUESTAO_ID);

            verify(repository).findByQuestionIdAndMockExamUserId(QUESTAO_ID, 7L);
            verify(repository, never()).findByQuestionId(any());
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
