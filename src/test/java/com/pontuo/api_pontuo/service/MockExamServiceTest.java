package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.dto.PerformanceItemResponseDTO;
import com.pontuo.api_pontuo.entity.AnswerOption;
import com.pontuo.api_pontuo.entity.MockExam;
import com.pontuo.api_pontuo.entity.MockExamHasQuestion;
import com.pontuo.api_pontuo.entity.Question;
import com.pontuo.api_pontuo.entity.User;
import com.pontuo.api_pontuo.repository.MockExamHasQuestionRepository;
import com.pontuo.api_pontuo.repository.MockExamRepository;
import com.pontuo.api_pontuo.repository.PerformanceRow;
import com.pontuo.api_pontuo.repository.UserRepository;
import com.pontuo.api_pontuo.security.CurrentUser;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MockExamService")
class MockExamServiceTest {

    private static final Long USER_ID = 7L;
    private static final Long OUTRO_ID = 8L;
    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 9, 12, 14, 0);

    @Mock
    private MockExamRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MockExamHasQuestionRepository linkRepository;

    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private MockExamService service;

    private User aluno;
    private User outroAluno;
    private MockExam simulado;

    @BeforeEach
    void setUp() {
        aluno = new User("maria", "maria@pontuo.com", LocalDate.of(2001, 3, 9), "$2a$10$hash");
        aluno.setId(USER_ID);
        outroAluno = new User("joao", "joao@pontuo.com", LocalDate.of(2002, 4, 1), "$2a$10$hash");
        outroAluno.setId(OUTRO_ID);
        simulado = new MockExam("Simulado ENEM", (short) 10, (short) 90, aluno);
        simulado.setId(1L);
    }

    private void comoEstudante() {
        lenient().when(currentUser.isAdministrador()).thenReturn(false);
        lenient().when(currentUser.get()).thenReturn(aluno);
    }

    private void simuladoExiste() {
        when(repository.findById(1L)).thenReturn(Optional.of(simulado));
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("sem userId, o dono é o usuário do token")
        void deveCriarParaQuemPediu() {
            comoEstudante();
            when(repository.save(any(MockExam.class))).thenAnswer(invocation -> invocation.getArgument(0));

            MockExam criado = service.create(new MockExam("Novo", (short) 10, (short) 90), null);

            assertSame(aluno, criado.getUser());
        }

        @Test
        @DisplayName("estudante informando o próprio userId é aceito")
        void deveAceitarOProprioUserId() {
            comoEstudante();

            service.create(simulado, USER_ID);

            verify(repository).save(simulado);
        }

        @Test
        @DisplayName("estudante não cria simulado em nome de outro")
        void deveRecusarUserIdDeOutro() {
            comoEstudante();

            assertThrows(AccessDeniedException.class, () -> service.create(simulado, OUTRO_ID));

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("administrador cria simulado para outro usuário")
        void administradorDeveCriarParaOutro() {
            when(currentUser.isAdministrador()).thenReturn(true);
            when(userRepository.findById(OUTRO_ID)).thenReturn(Optional.of(outroAluno));
            when(repository.save(any(MockExam.class))).thenAnswer(invocation -> invocation.getArgument(0));

            assertSame(outroAluno, service.create(simulado, OUTRO_ID).getUser());
        }

        @Test
        @DisplayName("administrador: falha quando o usuário não existe")
        void deveFalharQuandoUsuarioNaoExiste() {
            when(currentUser.isAdministrador()).thenReturn(true);
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.create(simulado, 99L));

            assertEquals("User não encontrado: id=99", ex.getMessage());
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        private MockExam atualizado;

        @BeforeEach
        void setUpAtualizado() {
            atualizado = new MockExam("Simulado revisado", (short) 12, (short) 120);
        }

        @Test
        @DisplayName("copia nome, número de questões e tempo; mantém o dono sem userId")
        void deveAtualizarCampos() {
            comoEstudante();
            simuladoExiste();
            when(linkRepository.countByMockExamId(1L)).thenReturn(3L);
            when(repository.save(any(MockExam.class))).thenAnswer(invocation -> invocation.getArgument(0));

            MockExam resultado = service.update(1L, atualizado, null);

            assertAll(
                    () -> assertEquals("Simulado revisado", resultado.getName()),
                    () -> assertEquals(12, resultado.getNumQuestions()),
                    () -> assertEquals(120, resultado.getMaxTime()),
                    () -> assertSame(aluno, resultado.getUser()));
        }

        @Test
        @DisplayName("recusa numQuestions menor que as questões já vinculadas")
        void deveRecusarReduzirAbaixoDosVinculos() {
            comoEstudante();
            simuladoExiste();
            when(linkRepository.countByMockExamId(1L)).thenReturn(13L);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.update(1L, atualizado, null));

            assertEquals("numQuestions não pode ser menor que as questões já vinculadas: 13", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("recusa alterar simulado finalizado")
        void deveRecusarSimuladoFinalizado() {
            comoEstudante();
            simulado.setStartedAt(INICIO);
            simulado.setFinishedAt(INICIO.plusMinutes(60));
            simuladoExiste();

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.update(1L, atualizado, null));

            assertEquals("simulado já finalizado não pode ser alterado: id=1", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("recusa alterar simulado de outro usuário")
        void deveRecusarSimuladoDeOutro() {
            simuladoExiste();
            doThrow(new AccessDeniedException("negado")).when(currentUser).checkAccess(aluno);

            assertThrows(AccessDeniedException.class, () -> service.update(1L, atualizado, null));

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("falha quando o simulado não existe")
        void deveFalharQuandoSimuladoNaoExiste() {
            when(repository.findById(404L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.update(404L, atualizado, null));

            assertEquals("MockExam não encontrado: id=404", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("remove o simulado, inclusive finalizado")
        void deveRemoverSimuladoFinalizado() {
            simulado.setStartedAt(INICIO);
            simulado.setFinishedAt(INICIO.plusMinutes(60));
            simuladoExiste();

            service.delete(1L);

            verify(currentUser).checkAccess(aluno);
            verify(repository).delete(simulado);
        }

        @Test
        @DisplayName("recusa remover simulado de outro usuário")
        void deveRecusarSimuladoDeOutro() {
            simuladoExiste();
            doThrow(new AccessDeniedException("negado")).when(currentUser).checkAccess(aluno);

            assertThrows(AccessDeniedException.class, () -> service.delete(1L));

            verify(repository, never()).delete(any());
        }

        @Test
        @DisplayName("falha quando o simulado não existe")
        void deveFalharQuandoSimuladoNaoExiste() {
            when(repository.findById(404L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.delete(404L));

            assertEquals("MockExam não encontrado: id=404", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("consultas")
    class Consultas {

        @Test
        @DisplayName("estudante lista só os próprios simulados")
        void estudanteDeveListarSoOsProprios() {
            comoEstudante();
            when(repository.findByUserId(USER_ID)).thenReturn(List.of(simulado));

            assertEquals(List.of(simulado), service.findAll());
            verify(repository, never()).findAll();
        }

        @Test
        @DisplayName("administrador lista todos")
        void administradorDeveListarTodos() {
            when(currentUser.isAdministrador()).thenReturn(true);

            service.findAll();

            verify(repository).findAll();
        }

        @Test
        @DisplayName("findByUserId verifica o acesso ao dono")
        void deveVerificarAcessoAoListarPorUsuario() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(aluno));
            when(repository.findByUserId(USER_ID)).thenReturn(List.of(simulado));

            assertEquals(List.of(simulado), service.findByUserId(USER_ID));
            verify(currentUser).checkAccess(aluno);
        }

        @Test
        @DisplayName("findByUserId falha quando o usuário não existe")
        void deveFalharQuandoUsuarioNaoExiste() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.findByUserId(99L));

            assertEquals("User não encontrado: id=99", ex.getMessage());
            verify(repository, never()).findByUserId(any());
        }

        @Test
        @DisplayName("findById recusa simulado de outro usuário")
        void findByIdDeveRecusarSimuladoDeOutro() {
            simuladoExiste();
            doThrow(new AccessDeniedException("negado")).when(currentUser).checkAccess(aluno);

            assertThrows(AccessDeniedException.class, () -> service.findById(1L));
        }
    }

    @Nested
    @DisplayName("start")
    class Start {

        @Test
        @DisplayName("registra o início")
        void deveRegistrarInicio() {
            simuladoExiste();
            when(repository.save(any(MockExam.class))).thenAnswer(invocation -> invocation.getArgument(0));

            assertNotNull(service.start(1L).getStartedAt());
        }

        @Test
        @DisplayName("recusa iniciar duas vezes")
        void deveRecusarSegundoInicio() {
            simulado.setStartedAt(INICIO);
            simuladoExiste();

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.start(1L));

            assertEquals("simulado já iniciado: id=1", ex.getMessage());
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("finish")
    class Finish {

        @BeforeEach
        void setUpInicio() {
            simulado.setStartedAt(INICIO);
        }

        private MockExamHasQuestion vinculo(Boolean acertou) {
            Question questao = new Question("Enunciado", null, (short) 2);
            MockExamHasQuestion vinculo = new MockExamHasQuestion(simulado, questao);
            if (acertou != null) {
                vinculo.answer(new AnswerOption("A", "Texto", acertou, questao), INICIO.plusMinutes(5));
            }
            return vinculo;
        }

        @Test
        @DisplayName("aproveitamento é acertos / numQuestions, com branco e não vinculada como erro")
        void deveCalcularTotais() {
            simuladoExiste();
            // 10 previstas, 4 vinculadas: 2 certas, 1 errada, 1 em branco.
            when(linkRepository.findByMockExamId(1L)).thenReturn(List.of(
                    vinculo(true),
                    vinculo(false),
                    vinculo(null),
                    vinculo(true)));
            when(repository.save(any(MockExam.class))).thenAnswer(invocation -> invocation.getArgument(0));

            MockExam finalizado = service.finish(1L);

            assertAll(
                    () -> assertNotNull(finalizado.getFinishedAt()),
                    () -> assertEquals((short) 3, finalizado.getAnsweredCount()),
                    () -> assertEquals((short) 2, finalizado.getCorrectCount()),
                    () -> assertEquals(new BigDecimal("20.00"), finalizado.getAccuracy()));
        }

        @Test
        @DisplayName("usa a correção congelada, não o gabarito atual")
        void deveUsarCorrecaoCongelada() {
            simuladoExiste();
            MockExamHasQuestion vinculo = vinculo(true);
            // O administrador corrigiu o gabarito depois da resposta.
            vinculo.getAnswerOption().setRightAnswer(false);
            when(linkRepository.findByMockExamId(1L)).thenReturn(List.of(vinculo));
            when(repository.save(any(MockExam.class))).thenAnswer(invocation -> invocation.getArgument(0));

            assertEquals((short) 1, service.finish(1L).getCorrectCount());
        }

        @Test
        @DisplayName("recusa finalizar simulado não iniciado")
        void deveRecusarSemInicio() {
            simulado.setStartedAt(null);
            simuladoExiste();

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.finish(1L));

            assertEquals("simulado ainda não foi iniciado: id=1", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("recusa finalizar duas vezes")
        void deveRecusarSegundaFinalizacao() {
            simulado.setFinishedAt(INICIO.plusMinutes(30));
            simuladoExiste();

            assertThrows(IllegalArgumentException.class, () -> service.finish(1L));

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("recusa finalizar simulado de outro usuário")
        void deveRecusarSimuladoDeOutro() {
            simuladoExiste();
            doThrow(new AccessDeniedException("negado")).when(currentUser).checkAccess(aluno);

            assertThrows(AccessDeniedException.class, () -> service.finish(1L));

            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("boletim")
    class Boletim {

        @Test
        @DisplayName("vazio enquanto o simulado não foi finalizado")
        void deveFicarVazioAntesDaFinalizacao() {
            simulado.setStartedAt(INICIO);
            simuladoExiste();

            assertEquals(List.of(), service.findSubjectResults(1L));
            verify(linkRepository, never()).performanceBySubjectOfMockExam(any());
        }

        @Test
        @DisplayName("calcula o aproveitamento de cada matéria sobre as questões vinculadas dela")
        void deveCalcularPorMateria() {
            simulado.setStartedAt(INICIO);
            simulado.setFinishedAt(INICIO.plusMinutes(30));
            simuladoExiste();
            when(linkRepository.performanceBySubjectOfMockExam(1L)).thenReturn(List.of(
                    new PerformanceRow(5L, "Biologia", 3L, 3L, 2L)));

            assertEquals(List.of(new PerformanceItemResponseDTO(5L, "Biologia", 3, 3, 2, new BigDecimal("66.67"))),
                    service.findSubjectResults(1L));
        }

        @Test
        @DisplayName("recusa o boletim de simulado de outro usuário")
        void deveRecusarSimuladoDeOutro() {
            simuladoExiste();
            doThrow(new AccessDeniedException("negado")).when(currentUser).checkAccess(aluno);

            assertThrows(AccessDeniedException.class, () -> service.findSubjectResults(1L));
        }
    }
}
