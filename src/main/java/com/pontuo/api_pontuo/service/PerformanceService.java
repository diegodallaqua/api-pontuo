package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.dto.EvolutionPointResponseDTO;
import com.pontuo.api_pontuo.dto.PerformanceFilterDTO;
import com.pontuo.api_pontuo.dto.PerformanceItemResponseDTO;
import com.pontuo.api_pontuo.dto.PerformanceOverviewResponseDTO;
import com.pontuo.api_pontuo.entity.MockExam;
import com.pontuo.api_pontuo.repository.EvolutionRow;
import com.pontuo.api_pontuo.repository.MockExamHasQuestionRepository;
import com.pontuo.api_pontuo.repository.MockExamRepository;
import com.pontuo.api_pontuo.repository.PerformanceRow;
import com.pontuo.api_pontuo.repository.UserRepository;
import com.pontuo.api_pontuo.security.CurrentUser;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Análise de desempenho do estudante. Só entram simulados finalizados, cujas
 * respostas estão travadas e corrigidas com o gabarito da época.
 *
 * <p>Dois denominadores, de propósito: no resumo e na evolução geral o
 * aproveitamento é acertos / numQuestions, como no simulado; nos
 * detalhamentos (área, matéria, tópico, dificuldade) é acertos / questões
 * vinculadas daquele grupo, já que numQuestions não se divide por matéria.
 *
 * <p>{@code userId} nulo significa o próprio usuário do token; informar outro
 * usuário é exclusivo do administrador.
 */
@Service
public class PerformanceService {

    private final MockExamRepository mockExamRepository;
    private final MockExamHasQuestionRepository linkRepository;
    private final UserRepository userRepository;
    private final CurrentUser currentUser;

    public PerformanceService(MockExamRepository mockExamRepository,
                              MockExamHasQuestionRepository linkRepository,
                              UserRepository userRepository,
                              CurrentUser currentUser) {
        this.mockExamRepository = mockExamRepository;
        this.linkRepository = linkRepository;
        this.userRepository = userRepository;
        this.currentUser = currentUser;
    }

    public PerformanceOverviewResponseDTO overview(Long userId, PerformanceFilterDTO filter) {
        List<MockExam> finished = mockExamRepository.findFinished(
                resolveUserId(userId), start(filter), end(filter));

        long questions = 0;
        long answered = 0;
        long correct = 0;
        long totalMinutes = 0;
        int timed = 0;
        for (MockExam mockExam : finished) {
            questions += mockExam.getNumQuestions();
            answered += valueOf(mockExam.getAnsweredCount());
            correct += valueOf(mockExam.getCorrectCount());
            // Simulados anteriores ao start/finish podem não ter início registrado.
            if (mockExam.getStartedAt() != null) {
                totalMinutes += Duration.between(mockExam.getStartedAt(), mockExam.getFinishedAt()).toMinutes();
                timed++;
            }
        }
        Long averageMinutes = timed == 0 ? null : Math.round((double) totalMinutes / timed);

        return new PerformanceOverviewResponseDTO(
                finished.size(), questions, answered, correct,
                Accuracy.of(correct, questions), averageMinutes);
    }

    public List<PerformanceItemResponseDTO> byKnowledgeArea(Long userId, PerformanceFilterDTO filter) {
        return toItems(linkRepository.performanceByKnowledgeArea(
                resolveUserId(userId), start(filter), end(filter), filter.entranceExamId()));
    }

    public List<PerformanceItemResponseDTO> bySubject(Long userId, PerformanceFilterDTO filter,
                                                      Long knowledgeAreaId) {
        return toItems(linkRepository.performanceBySubject(
                resolveUserId(userId), start(filter), end(filter), filter.entranceExamId(),
                knowledgeAreaId));
    }

    public List<PerformanceItemResponseDTO> byTopic(Long userId, PerformanceFilterDTO filter,
                                                    Long subjectId) {
        return toItems(linkRepository.performanceByTopic(
                resolveUserId(userId), start(filter), end(filter), filter.entranceExamId(),
                subjectId));
    }

    public List<PerformanceItemResponseDTO> byDifficulty(Long userId, PerformanceFilterDTO filter) {
        return toItems(linkRepository.performanceByDifficulty(
                resolveUserId(userId), start(filter), end(filter), filter.entranceExamId()));
    }

    /**
     * Tópicos com menor aproveitamento. {@code minQuestions} descarta tópicos
     * com poucas questões, em que um único erro derrubaria a porcentagem.
     */
    public List<PerformanceItemResponseDTO> weakTopics(Long userId, PerformanceFilterDTO filter,
                                                       int minQuestions, int limit) {
        return byTopic(userId, filter, null).stream()
                .filter(item -> item.questions() >= minQuestions)
                .sorted(Comparator.comparing(PerformanceItemResponseDTO::accuracy)
                        .thenComparing(PerformanceItemResponseDTO::questions, Comparator.reverseOrder()))
                .limit(limit)
                .toList();
    }

    /**
     * Resultado de cada simulado finalizado, em ordem cronológica. Com
     * {@code subjectId}, o resultado é o daquela matéria em cada simulado,
     * com aproveitamento sobre as questões vinculadas da matéria; simulados
     * sem questão da matéria ficam de fora.
     */
    public List<EvolutionPointResponseDTO> evolution(Long userId, PerformanceFilterDTO filter,
                                                     Long subjectId) {
        Long ownerId = resolveUserId(userId);
        if (subjectId == null) {
            return mockExamRepository.findFinished(ownerId, start(filter), end(filter)).stream()
                    .map(PerformanceService::toPoint)
                    .toList();
        }
        return linkRepository.evolutionBySubject(ownerId, subjectId, start(filter), end(filter)).stream()
                .map(PerformanceService::toPoint)
                .toList();
    }

    private Long resolveUserId(Long userId) {
        if (userId == null) {
            return currentUser.get().getId();
        }
        if (!currentUser.isAdministrador()) {
            throw new AccessDeniedException(
                    "Acesso negado: o estudante só consulta o próprio desempenho.");
        }
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User não encontrado: id=" + userId);
        }
        return userId;
    }

    private static LocalDateTime start(PerformanceFilterDTO filter) {
        LocalDate from = filter.from();
        return from == null ? null : from.atStartOfDay();
    }

    /** {@code to} é inclusivo para o cliente; a consulta usa o dia seguinte, exclusivo. */
    private static LocalDateTime end(PerformanceFilterDTO filter) {
        LocalDate to = filter.to();
        return to == null ? null : to.plusDays(1).atStartOfDay();
    }

    private static long valueOf(Short value) {
        return value == null ? 0 : value;
    }

    /** Também usado pelo boletim do simulado, para os dois saírem no mesmo formato. */
    static List<PerformanceItemResponseDTO> toItems(List<PerformanceRow> rows) {
        return rows.stream()
                .map(row -> new PerformanceItemResponseDTO(
                        row.id(), row.description(),
                        row.questions(), row.answered(), row.correct(),
                        Accuracy.of(row.correct(), row.questions())))
                .toList();
    }

    private static EvolutionPointResponseDTO toPoint(MockExam mockExam) {
        return new EvolutionPointResponseDTO(
                mockExam.getId(), mockExam.getName(), mockExam.getFinishedAt(),
                mockExam.getNumQuestions(), valueOf(mockExam.getAnsweredCount()),
                valueOf(mockExam.getCorrectCount()), mockExam.getAccuracy());
    }

    private static EvolutionPointResponseDTO toPoint(EvolutionRow row) {
        return new EvolutionPointResponseDTO(
                row.mockExamId(), row.mockExamName(), row.finishedAt(),
                row.questions(), row.answered(), row.correct(),
                Accuracy.of(row.correct(), row.questions()));
    }
}
