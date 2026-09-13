package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.MockExam;
import com.pontuo.api_pontuo.entity.User;
import com.pontuo.api_pontuo.repository.MockExamRepository;
import com.pontuo.api_pontuo.repository.UserRepository;
import com.pontuo.api_pontuo.support.Pendente;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MockExamService")
class MockExamServiceTest {

    private static final Long USER_ID = 7L;
    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 9, 12, 14, 0);

    @Mock
    private MockExamRepository repository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MockExamService service;

    private User aluno;
    private MockExam simulado;

    @BeforeEach
    void setUp() {
        aluno = new User("maria", "maria@pontuo.com", LocalDate.of(2001, 3, 9), "$2a$10$hash");
        aluno.setId(USER_ID);
        simulado = new MockExam("Simulado ENEM", (short) 10, (short) 90);
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("associa o usuário e persiste o simulado")
        void deveCriarSimuladoDoUsuario() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(aluno));
            when(repository.save(any(MockExam.class))).thenAnswer(invocation -> invocation.getArgument(0));

            MockExam criado = service.create(simulado, USER_ID);

            assertSame(aluno, criado.getUser());
            verify(repository).save(simulado);
        }

        @Test
        @DisplayName("rejeita finishedAt sem startedAt")
        void deveRejeitarFimSemInicio() {
            simulado.setFinishedAt(INICIO);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.create(simulado, USER_ID));

            assertEquals("finishedAt não pode ser informado sem startedAt", ex.getMessage());
            verifyNoInteractions(repository, userRepository);
        }

        @Test
        @DisplayName("rejeita finishedAt anterior a startedAt")
        void deveRejeitarFimAntesDoInicio() {
            simulado.setStartedAt(INICIO);
            simulado.setFinishedAt(INICIO.minusMinutes(1));

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.create(simulado, USER_ID));

            assertEquals("finishedAt não pode ser anterior a startedAt", ex.getMessage());
            verifyNoInteractions(repository, userRepository);
        }

        @Test
        @DisplayName("aceita finishedAt igual a startedAt (limite)")
        void deveAceitarFimIgualAoInicio() {
            simulado.setStartedAt(INICIO);
            simulado.setFinishedAt(INICIO);
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(aluno));

            service.create(simulado, USER_ID);

            verify(repository).save(simulado);
        }

        @Test
        @DisplayName("rejeita correctCount maior que numQuestions")
        void deveRejeitarAcertosAcimaDoTotal() {
            simulado.setCorrectCount((short) 11);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.create(simulado, USER_ID));

            assertEquals("correctCount não pode ser maior que numQuestions", ex.getMessage());
            verifyNoInteractions(repository, userRepository);
        }

        @Test
        @DisplayName("aceita correctCount igual a numQuestions (limite)")
        void deveAceitarAcertosIguaisAoTotal() {
            simulado.setCorrectCount((short) 10);
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(aluno));

            service.create(simulado, USER_ID);

            verify(repository).save(simulado);
        }

        @Test
        @DisplayName("falha quando o usuário não existe")
        void deveFalharQuandoUsuarioNaoExiste() {
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

        private MockExam existente;
        private MockExam atualizado;

        @BeforeEach
        void setUpExistente() {
            existente = new MockExam("Simulado antigo", (short) 5, (short) 30);
            existente.setId(1L);

            atualizado = new MockExam("Simulado ENEM", (short) 10, (short) 90);
            atualizado.setStartedAt(INICIO);
            atualizado.setFinishedAt(INICIO.plusMinutes(80));
            atualizado.setCorrectCount((short) 7);
            atualizado.setAccuracy(new BigDecimal("70.00"));
        }

        @Test
        @DisplayName("copia todos os campos para o simulado existente")
        void deveAtualizarTodosOsCampos() {
            when(repository.findById(1L)).thenReturn(Optional.of(existente));
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(aluno));
            when(repository.save(any(MockExam.class))).thenAnswer(invocation -> invocation.getArgument(0));

            MockExam resultado = service.update(1L, atualizado, USER_ID);

            assertAll(
                    () -> assertEquals(1L, resultado.getId()),
                    () -> assertEquals("Simulado ENEM", resultado.getName()),
                    () -> assertEquals(10, resultado.getNumQuestions()),
                    () -> assertEquals(90, resultado.getMaxTime()),
                    () -> assertEquals(INICIO, resultado.getStartedAt()),
                    () -> assertEquals(INICIO.plusMinutes(80), resultado.getFinishedAt()),
                    () -> assertEquals(7, resultado.getCorrectCount()),
                    () -> assertEquals(new BigDecimal("70.00"), resultado.getAccuracy()),
                    () -> assertSame(aluno, resultado.getUser()));
        }

        @Test
        @DisplayName("falha quando o simulado não existe")
        void deveFalharQuandoSimuladoNaoExiste() {
            when(repository.findById(404L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.update(404L, atualizado, USER_ID));

            assertEquals("MockExam não encontrado: id=404", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("não salva quando os novos dados violam as regras")
        void naoDeveSalvarDadosInvalidos() {
            atualizado.setCorrectCount((short) 11);
            when(repository.findById(1L)).thenReturn(Optional.of(existente));

            assertThrows(IllegalArgumentException.class, () -> service.update(1L, atualizado, USER_ID));

            verify(repository, never()).save(any());
            verifyNoInteractions(userRepository);
        }

        @Test
        @DisplayName("falha quando o novo usuário não existe")
        void deveFalharQuandoUsuarioNaoExiste() {
            when(repository.findById(1L)).thenReturn(Optional.of(existente));
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.update(1L, atualizado, 99L));

            assertEquals("User não encontrado: id=99", ex.getMessage());
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("remove o simulado existente")
        void deveRemoverSimuladoExistente() {
            when(repository.existsById(1L)).thenReturn(true);

            service.delete(1L);

            verify(repository).deleteById(1L);
        }

        @Test
        @DisplayName("falha quando o simulado não existe")
        void deveFalharQuandoSimuladoNaoExiste() {
            when(repository.existsById(404L)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.delete(404L));

            assertEquals("MockExam não encontrado: id=404", ex.getMessage());
            verify(repository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("findByUserId")
    class FindByUserId {

        @Test
        @DisplayName("lista os simulados do usuário")
        void deveListarSimuladosDoUsuario() {
            when(userRepository.existsById(USER_ID)).thenReturn(true);
            when(repository.findByUserId(USER_ID)).thenReturn(List.of(simulado));

            assertEquals(List.of(simulado), service.findByUserId(USER_ID));
        }

        @Test
        @DisplayName("falha quando o usuário não existe")
        void deveFalharQuandoUsuarioNaoExiste() {
            when(userRepository.existsById(99L)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.findByUserId(99L));

            assertEquals("User não encontrado: id=99", ex.getMessage());
            verify(repository, never()).findByUserId(any());
        }
    }

    @Nested
    @DisplayName("accuracy (regra pendente)")
    class Accuracy {

        @Test
        @Pendente("validar accuracy contra correctCount / numQuestions")
        @DisplayName("rejeita accuracy incoerente com os acertos")
        void deveRejeitarAccuracyIncoerente() {
            simulado.setCorrectCount((short) 5);
            simulado.setAccuracy(new BigDecimal("90.00"));
            // lenient: a implementação pode validar antes de buscar o usuário.
            lenient().when(userRepository.findById(USER_ID)).thenReturn(Optional.of(aluno));

            assertThrows(IllegalArgumentException.class, () -> service.create(simulado, USER_ID));

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("aceita accuracy coerente, arredondada em duas casas")
        void deveAceitarAccuracyCoerente() {
            MockExam tresQuestoes = new MockExam("Simulado curto", (short) 3, (short) 15);
            tresQuestoes.setCorrectCount((short) 1);
            tresQuestoes.setAccuracy(new BigDecimal("33.33"));
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(aluno));

            service.create(tresQuestoes, USER_ID);

            verify(repository).save(tresQuestoes);
        }
    }
}
