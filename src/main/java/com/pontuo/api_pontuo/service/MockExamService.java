package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.dto.PerformanceItemResponseDTO;
import com.pontuo.api_pontuo.entity.MockExam;
import com.pontuo.api_pontuo.entity.MockExamHasQuestion;
import com.pontuo.api_pontuo.entity.User;
import com.pontuo.api_pontuo.repository.MockExamHasQuestionRepository;
import com.pontuo.api_pontuo.repository.MockExamRepository;
import com.pontuo.api_pontuo.repository.UserRepository;
import com.pontuo.api_pontuo.security.CurrentUser;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Ciclo de vida do simulado: criado, iniciado (start) e finalizado (finish).
 * Na finalização o servidor corrige, grava os totais e trava o simulado — daí
 * em diante nem ele nem as respostas mudam, o que mantém o histórico de
 * desempenho estável. Excluir continua permitido.
 *
 * <p>O estudante só acessa os próprios simulados; o administrador, todos.
 */
@Service
public class MockExamService {

    private final MockExamRepository repository;
    private final UserRepository userRepository;
    private final MockExamHasQuestionRepository linkRepository;
    private final CurrentUser currentUser;

    public MockExamService(MockExamRepository repository,
                           UserRepository userRepository,
                           MockExamHasQuestionRepository linkRepository,
                           CurrentUser currentUser) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.linkRepository = linkRepository;
        this.currentUser = currentUser;
    }

    /** O administrador vê todos os simulados; o estudante, só os dele. */
    public List<MockExam> findAll() {
        if (currentUser.isAdministrador()) {
            return repository.findAll();
        }
        return repository.findByUserId(currentUser.get().getId());
    }

    public List<MockExam> findByUserId(Long userId) {
        User user = findUser(userId);
        currentUser.checkAccess(user);
        return repository.findByUserId(userId);
    }

    public Optional<MockExam> findById(Long id) {
        Optional<MockExam> mockExam = repository.findById(id);
        mockExam.ifPresent(found -> currentUser.checkAccess(found.getUser()));
        return mockExam;
    }

    public MockExam create(MockExam mockExam, Long userId) {
        mockExam.setUser(resolveOwner(userId));
        return repository.save(mockExam);
    }

    public MockExam update(Long id, MockExam updated, Long userId) {
        MockExam existing = findAccessible(id);
        checkNotFinished(existing);

        long linked = linkRepository.countByMockExamId(id);
        if (updated.getNumQuestions() < linked) {
            throw new IllegalArgumentException(
                    "numQuestions não pode ser menor que as questões já vinculadas: " + linked);
        }

        existing.setName(updated.getName());
        existing.setNumQuestions(updated.getNumQuestions());
        existing.setMaxTime(updated.getMaxTime());
        if (userId != null) {
            existing.setUser(resolveOwner(userId));
        }
        return repository.save(existing);
    }

    /**
     * O banco apagaria os vínculos em cascata, mas removê-los pela JPA evita
     * que instâncias já carregadas na sessão apontem para um simulado apagado.
     */
    @Transactional
    public void delete(Long id) {
        MockExam existing = findAccessible(id);
        linkRepository.deleteAll(linkRepository.findByMockExamId(id));
        repository.delete(existing);
    }

    public MockExam start(Long id) {
        MockExam mockExam = findAccessible(id);
        if (mockExam.isStarted()) {
            throw new IllegalArgumentException("simulado já iniciado: id=" + id);
        }
        mockExam.setStartedAt(LocalDateTime.now());
        return repository.save(mockExam);
    }

    /**
     * Corrige e trava o simulado. O aproveitamento é acertos / numQuestions:
     * questão em branco, ou que nem chegou a ser vinculada, conta como erro.
     */
    @Transactional
    public MockExam finish(Long id) {
        MockExam mockExam = findAccessible(id);
        if (!mockExam.isStarted()) {
            throw new IllegalArgumentException("simulado ainda não foi iniciado: id=" + id);
        }
        checkNotFinished(mockExam);

        List<MockExamHasQuestion> links = linkRepository.findByMockExamId(id);
        long answered = links.stream().filter(link -> link.getAnswerOption() != null).count();
        long correct = links.stream().filter(link -> Boolean.TRUE.equals(link.getIsCorrect())).count();

        mockExam.setFinishedAt(LocalDateTime.now());
        mockExam.setAnsweredCount((short) answered);
        mockExam.setCorrectCount((short) correct);
        mockExam.setAccuracy(Accuracy.of(correct, mockExam.getNumQuestions()));
        return repository.save(mockExam);
    }

    /**
     * Aproveitamento por matéria do simulado, calculado dos vínculos com a
     * mesma regra de matéria da análise de desempenho. Vazio enquanto o
     * simulado não foi finalizado: antes disso não há boletim.
     */
    public List<PerformanceItemResponseDTO> findSubjectResults(Long id) {
        MockExam mockExam = findAccessible(id);
        if (!mockExam.isFinished()) {
            return List.of();
        }
        return PerformanceService.toItems(linkRepository.performanceBySubjectOfMockExam(id));
    }

    private MockExam findAccessible(Long id) {
        MockExam mockExam = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "MockExam não encontrado: id=" + id));
        currentUser.checkAccess(mockExam.getUser());
        return mockExam;
    }

    private void checkNotFinished(MockExam mockExam) {
        if (mockExam.isFinished()) {
            throw new IllegalArgumentException(
                    "simulado já finalizado não pode ser alterado: id=" + mockExam.getId());
        }
    }

    /**
     * Sem userId, o dono é quem fez a requisição. Só o administrador cria ou
     * transfere simulado para outra pessoa.
     */
    private User resolveOwner(Long userId) {
        if (currentUser.isAdministrador()) {
            return userId == null ? currentUser.get() : findUser(userId);
        }
        User me = currentUser.get();
        if (userId != null && !userId.equals(me.getId())) {
            throw new AccessDeniedException(
                    "Acesso negado: o estudante só cria simulados para si mesmo.");
        }
        return me;
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User não encontrado: id=" + userId));
    }
}
