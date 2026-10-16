package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.AnswerOption;
import com.pontuo.api_pontuo.entity.MockExam;
import com.pontuo.api_pontuo.entity.MockExamHasQuestion;
import com.pontuo.api_pontuo.entity.Question;
import com.pontuo.api_pontuo.repository.AnswerOptionRepository;
import com.pontuo.api_pontuo.repository.MockExamHasQuestionRepository;
import com.pontuo.api_pontuo.repository.MockExamRepository;
import com.pontuo.api_pontuo.repository.QuestionRepository;
import com.pontuo.api_pontuo.security.CurrentUser;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Questões de um simulado e as respostas do estudante. Vincular questões pode
 * ser feito antes do início; responder exige o simulado iniciado. Depois da
 * finalização nada muda. O estudante só mexe nos próprios simulados.
 */
@Service
public class MockExamHasQuestionService {

    private final MockExamHasQuestionRepository repository;
    private final MockExamRepository mockExamRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final CurrentUser currentUser;

    public MockExamHasQuestionService(MockExamHasQuestionRepository repository,
                                      MockExamRepository mockExamRepository,
                                      QuestionRepository questionRepository,
                                      AnswerOptionRepository answerOptionRepository,
                                      CurrentUser currentUser) {
        this.repository = repository;
        this.mockExamRepository = mockExamRepository;
        this.questionRepository = questionRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.currentUser = currentUser;
    }

    /** O administrador vê todos os vínculos; o estudante, os dos simulados dele. */
    public List<MockExamHasQuestion> findAll() {
        if (currentUser.isAdministrador()) {
            return repository.findAll();
        }
        return repository.findByMockExamUserId(currentUser.get().getId());
    }

    public List<MockExamHasQuestion> findByMockExamId(Long mockExamId) {
        MockExam mockExam = findMockExam(mockExamId);
        currentUser.checkAccess(mockExam.getUser());
        return repository.findByMockExamId(mockExamId);
    }

    public List<MockExamHasQuestion> findByQuestionId(Long questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new EntityNotFoundException("Question não encontrada: id=" + questionId);
        }
        if (currentUser.isAdministrador()) {
            return repository.findByQuestionId(questionId);
        }
        return repository.findByQuestionIdAndMockExamUserId(questionId, currentUser.get().getId());
    }

    public Optional<MockExamHasQuestion> findById(Long id) {
        Optional<MockExamHasQuestion> link = repository.findById(id);
        link.ifPresent(found -> currentUser.checkAccess(found.getMockExam().getUser()));
        return link;
    }

    public MockExamHasQuestion create(Long mockExamId, Long questionId, Long answerOptionId) {
        MockExam mockExam = findEditableMockExam(mockExamId);
        validateNotLinked(mockExamId, questionId);
        validateLimit(mockExam);

        MockExamHasQuestion link = new MockExamHasQuestion(mockExam, findQuestion(questionId));
        if (answerOptionId != null) {
            validateStarted(mockExam);
            link.answer(findAnswerOption(answerOptionId, questionId), LocalDateTime.now());
        }
        return repository.save(link);
    }

    public MockExamHasQuestion update(Long id, Long mockExamId, Long questionId, Long answerOptionId) {
        MockExamHasQuestion existing = findEditable(id);

        boolean sameMockExam = existing.getMockExam().getId().equals(mockExamId);
        boolean samePair = sameMockExam && existing.getQuestion().getId().equals(questionId);
        MockExam mockExam = sameMockExam ? existing.getMockExam() : findEditableMockExam(mockExamId);
        if (!samePair) {
            validateNotLinked(mockExamId, questionId);
        }
        if (!sameMockExam) {
            validateLimit(mockExam);
        }

        existing.setMockExam(mockExam);
        existing.setQuestion(findQuestion(questionId));
        if (answerOptionId == null) {
            existing.answer(null, null);
        } else if (!samePair || !answerOptionId.equals(answeredOptionId(existing))) {
            // Trocar a alternativa registra novo horário; repetir a mesma mantém o original.
            validateStarted(mockExam);
            existing.answer(findAnswerOption(answerOptionId, questionId), LocalDateTime.now());
        }
        return repository.save(existing);
    }

    public void delete(Long id) {
        MockExamHasQuestion existing = findEditable(id);
        repository.delete(existing);
    }

    private MockExamHasQuestion findEditable(Long id) {
        MockExamHasQuestion link = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "MockExamHasQuestion não encontrado: id=" + id));
        currentUser.checkAccess(link.getMockExam().getUser());
        validateNotFinished(link.getMockExam());
        return link;
    }

    private MockExam findEditableMockExam(Long mockExamId) {
        MockExam mockExam = findMockExam(mockExamId);
        currentUser.checkAccess(mockExam.getUser());
        validateNotFinished(mockExam);
        return mockExam;
    }

    private static Long answeredOptionId(MockExamHasQuestion link) {
        return link.getAnswerOption() == null ? null : link.getAnswerOption().getId();
    }

    // A mesma questão não pode aparecer duas vezes no mesmo simulado.
    private void validateNotLinked(Long mockExamId, Long questionId) {
        if (repository.existsByMockExamIdAndQuestionId(mockExamId, questionId)) {
            throw new IllegalArgumentException(
                    "questão já vinculada ao simulado: mockExamId=" + mockExamId
                            + ", questionId=" + questionId);
        }
    }

    // O simulado não recebe mais questões do que numQuestions.
    private void validateLimit(MockExam mockExam) {
        if (repository.countByMockExamId(mockExam.getId()) >= mockExam.getNumQuestions()) {
            throw new IllegalArgumentException(
                    "simulado já tem as " + mockExam.getNumQuestions()
                            + " questões previstas em numQuestions: mockExamId=" + mockExam.getId());
        }
    }

    private static void validateStarted(MockExam mockExam) {
        if (!mockExam.isStarted()) {
            throw new IllegalArgumentException(
                    "simulado ainda não foi iniciado: mockExamId=" + mockExam.getId());
        }
    }

    private static void validateNotFinished(MockExam mockExam) {
        if (mockExam.isFinished()) {
            throw new IllegalArgumentException(
                    "simulado já finalizado não pode ser alterado: mockExamId=" + mockExam.getId());
        }
    }

    private MockExam findMockExam(Long mockExamId) {
        return mockExamRepository.findById(mockExamId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "MockExam não encontrado: id=" + mockExamId));
    }

    private Question findQuestion(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Question não encontrada: id=" + questionId));
    }

    // A alternativa marcada tem que ser uma das alternativas da própria questão.
    private AnswerOption findAnswerOption(Long answerOptionId, Long questionId) {
        AnswerOption answerOption = answerOptionRepository.findById(answerOptionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "AnswerOption não encontrada: id=" + answerOptionId));
        if (!Objects.equals(answerOption.getQuestion().getId(), questionId)) {
            throw new IllegalArgumentException(
                    "answerOptionId=" + answerOptionId
                            + " não pertence à questão: questionId=" + questionId);
        }
        return answerOption;
    }
}
