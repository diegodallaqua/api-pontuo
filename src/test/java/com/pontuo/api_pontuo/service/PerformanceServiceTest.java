package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.dto.EvolutionPointResponseDTO;
import com.pontuo.api_pontuo.dto.PerformanceFilterDTO;
import com.pontuo.api_pontuo.dto.PerformanceItemResponseDTO;
import com.pontuo.api_pontuo.dto.PerformanceOverviewResponseDTO;
import com.pontuo.api_pontuo.entity.MockExam;
import com.pontuo.api_pontuo.entity.User;
import com.pontuo.api_pontuo.repository.MockExamHasQuestionRepository;
import com.pontuo.api_pontuo.repository.MockExamRepository;
import com.pontuo.api_pontuo.repository.EvolutionRow;
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PerformanceService")
class PerformanceServiceTest {

    private static final Long USER_ID = 7L;
    private static final PerformanceFilterDTO SEM_FILTRO = new PerformanceFilterDTO(null, null, null);
    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 9, 12, 14, 0);

    @Mock
    private MockExamRepository mockExamRepository;

    @Mock
    private MockExamHasQuestionRepository linkRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private PerformanceService service;

    private User aluno;

    @BeforeEach
    void setUp() {
        aluno = new User("maria", "maria@pontuo.com", LocalDate.of(2001, 3, 9), "$2a$10$hash");
        aluno.setId(USER_ID);
    }

    private static MockExam finalizado(int numQuestions, int respondidas, int acertos, int minutos) {
        MockExam simulado = new MockExam("Simulado", (short) numQuestions, (short) 90);
        simulado.setStartedAt(INICIO);
        simulado.setFinishedAt(INICIO.plusMinutes(minutos));
        simulado.setAnsweredCount((short) respondidas);
        simulado.setCorrectCount((short) acertos);
        return simulado;
    }

    @Nested
    @DisplayName("de quem é o desempenho")
    class Dono {

        @Test
        @DisplayName("sem userId, consulta o usuário do token")
        void deveUsarOUsuarioDoToken() {
            when(currentUser.get()).thenReturn(aluno);

            service.overview(null, SEM_FILTRO);

            verify(mockExamRepository).findFinished(USER_ID, null, null);
        }

        @Test
        @DisplayName("estudante não consulta outro usuário")
        void estudanteNaoDeveConsultarOutro() {
            when(currentUser.isAdministrador()).thenReturn(false);

            assertThrows(AccessDeniedException.class, () -> service.overview(8L, SEM_FILTRO));

            verifyNoInteractions(mockExamRepository);
        }

        @Test
        @DisplayName("administrador consulta outro usuário")
        void administradorDeveConsultarOutro() {
            when(currentUser.isAdministrador()).thenReturn(true);
            when(userRepository.existsById(8L)).thenReturn(true);

            service.bySubject(8L, SEM_FILTRO, null);

            verify(linkRepository).performanceBySubject(8L, null, null, null, null);
        }

        @Test
        @DisplayName("administrador: falha quando o usuário não existe")
        void deveFalharQuandoUsuarioNaoExiste() {
            when(currentUser.isAdministrador()).thenReturn(true);
            when(userRepository.existsById(99L)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> service.overview(99L, SEM_FILTRO));

            assertEquals("User não encontrado: id=99", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("overview")
    class Overview {

        @Test
        @DisplayName("soma os simulados e calcula acertos / numQuestions")
        void deveSomarSimulados() {
            when(currentUser.get()).thenReturn(aluno);
            when(mockExamRepository.findFinished(USER_ID, null, null)).thenReturn(List.of(
                    finalizado(10, 9, 6, 50),
                    finalizado(20, 15, 10, 100)));

            PerformanceOverviewResponseDTO resumo = service.overview(null, SEM_FILTRO);

            assertAll(
                    () -> assertEquals(2, resumo.finishedMockExams()),
                    () -> assertEquals(30, resumo.questions()),
                    () -> assertEquals(24, resumo.answered()),
                    () -> assertEquals(16, resumo.correct()),
                    () -> assertEquals(new BigDecimal("53.33"), resumo.accuracy()),
                    () -> assertEquals(75L, resumo.averageDurationMinutes()));
        }

        @Test
        @DisplayName("sem simulados, aproveitamento e duração ficam nulos")
        void deveFicarVazioSemSimulados() {
            when(currentUser.get()).thenReturn(aluno);
            when(mockExamRepository.findFinished(USER_ID, null, null)).thenReturn(List.of());

            PerformanceOverviewResponseDTO resumo = service.overview(null, SEM_FILTRO);

            assertAll(
                    () -> assertEquals(0, resumo.finishedMockExams()),
                    () -> assertNull(resumo.accuracy()),
                    () -> assertNull(resumo.averageDurationMinutes()));
        }

        @Test
        @DisplayName("o período é inclusivo: to vira o início do dia seguinte")
        void deveConverterPeriodo() {
            when(currentUser.get()).thenReturn(aluno);

            service.overview(null, new PerformanceFilterDTO(
                    LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30), null));

            verify(mockExamRepository).findFinished(USER_ID,
                    LocalDateTime.of(2026, 9, 1, 0, 0), LocalDateTime.of(2026, 10, 1, 0, 0));
        }
    }

    @Nested
    @DisplayName("detalhamentos")
    class Detalhamentos {

        @Test
        @DisplayName("aproveitamento do grupo é acertos / questões vinculadas")
        void deveCalcularAproveitamentoDoGrupo() {
            when(currentUser.get()).thenReturn(aluno);
            when(linkRepository.performanceByKnowledgeArea(USER_ID, null, null, 1L)).thenReturn(List.of(
                    new PerformanceRow(3L, "Ciências da Natureza", 3L, 2L, 1L)));

            List<PerformanceItemResponseDTO> itens =
                    service.byKnowledgeArea(null, new PerformanceFilterDTO(null, null, 1L));

            assertEquals(List.of(new PerformanceItemResponseDTO(
                    3L, "Ciências da Natureza", 3, 2, 1, new BigDecimal("33.33"))), itens);
        }

        @Test
        @DisplayName("tópicos fracos: descarta os com poucas questões e ordena do pior para o melhor")
        void deveListarTopicosFracos() {
            when(currentUser.get()).thenReturn(aluno);
            when(linkRepository.performanceByTopic(USER_ID, null, null, null, null)).thenReturn(List.of(
                    new PerformanceRow(1L, "Genética", 10L, 10L, 8L),
                    new PerformanceRow(2L, "Ecologia", 2L, 2L, 0L),
                    new PerformanceRow(3L, "Citologia", 5L, 5L, 2L),
                    new PerformanceRow(4L, "Evolução", 6L, 6L, 3L)));

            List<PerformanceItemResponseDTO> fracos = service.weakTopics(null, SEM_FILTRO, 5, 2);

            assertEquals(List.of("Citologia", "Evolução"),
                    fracos.stream().map(PerformanceItemResponseDTO::description).toList());
        }
    }

    @Nested
    @DisplayName("evolution")
    class Evolution {

        @Test
        @DisplayName("sem matéria, usa o resultado geral de cada simulado")
        void deveUsarResultadoGeral() {
            MockExam simulado = finalizado(10, 8, 7, 40);
            simulado.setAccuracy(new BigDecimal("70.00"));
            when(currentUser.get()).thenReturn(aluno);
            when(mockExamRepository.findFinished(USER_ID, null, null)).thenReturn(List.of(simulado));

            List<EvolutionPointResponseDTO> pontos = service.evolution(null, SEM_FILTRO, null);

            assertAll(
                    () -> assertEquals(1, pontos.size()),
                    () -> assertEquals(10, pontos.get(0).questions()),
                    () -> assertEquals(new BigDecimal("70.00"), pontos.get(0).accuracy()));
            verify(linkRepository, never()).evolutionBySubject(anyLong(), anyLong(), any(), any());
        }

        @Test
        @DisplayName("com matéria, calcula acertos / questões da matéria em cada simulado")
        void deveUsarResultadoPorMateria() {
            LocalDateTime fim = INICIO.plusMinutes(40);
            when(currentUser.get()).thenReturn(aluno);
            when(linkRepository.evolutionBySubject(USER_ID, 5L, null, null)).thenReturn(List.of(
                    new EvolutionRow(1L, "Simulado", fim, 3L, 3L, 2L)));

            List<EvolutionPointResponseDTO> pontos = service.evolution(null, SEM_FILTRO, 5L);

            assertEquals(List.of(new EvolutionPointResponseDTO(1L, "Simulado", fim, 3, 3, 2, new BigDecimal("66.67"))),
                    pontos);
            verify(mockExamRepository, never()).findFinished(any(), any(), any());
        }
    }
}
