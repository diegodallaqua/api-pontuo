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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MockExamHasQuestionService {

    private final MockExamHasQuestionRepository repository;
    private final MockExamRepository mockExamRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;

    public MockExamHasQuestionService(MockExamHasQuestionRepository repository,
                                      MockExamRepository mockExamRepository,
                                      QuestionRepository questionRepository,
                                      AnswerOptionRepository answerOptionRepository) {
        this.repository = repository;
        this.mockExamRepository = mockExamRepository;
        this.questionRepository = questionRepository;
        this.answerOptionRepository = answerOptionRepository;
    }

    public List<MockExamHasQuestion> findAll() {
        return repository.findAll();
    }

    public List<MockExamHasQuestion> findByMockExamId(Long mockExamId) {
        if (!mockExamRepository.existsById(mockExamId)) {
            throw new EntityNotFoundException("MockExam não encontrado: id=" + mockExamId);
        }
        return repository.findByMockExamId(mockExamId);
    }

    public List<MockExamHasQuestion> findByQuestionId(Long questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new EntityNotFoundException("Question não encontrada: id=" + questionId);
        }
        return repository.findByQuestionId(questionId);
    }

    public Optional<MockExamHasQuestion> findById(Long id) {
        return repository.findById(id);
    }

    public MockExamHasQuestion create(Long mockExamId, Long questionId,
                                      Long answerOptionId, LocalDateTime answeredAt) {
        validateNotLinked(mockExamId, questionId);
        validateAnswer(answerOptionId, answeredAt);

        return repository.save(new MockExamHasQuestion(
                findMockExam(mockExamId),
                findQuestion(questionId),
                findAnswerOption(answerOptionId, questionId),
                answeredAt));
    }

    public MockExamHasQuestion update(Long id, Long mockExamId, Long questionId,
                                      Long answerOptionId, LocalDateTime answeredAt) {
        MockExamHasQuestion existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "MockExamHasQuestion não encontrado: id=" + id));

        boolean samePair = existing.getMockExam().getId().equals(mockExamId)
                && existing.getQuestion().getId().equals(questionId);
        if (!samePair) {
            validateNotLinked(mockExamId, questionId);
        }
        validateAnswer(answerOptionId, answeredAt);

        existing.setMockExam(findMockExam(mockExamId));
        existing.setQuestion(findQuestion(questionId));
        existing.setAnswerOption(findAnswerOption(answerOptionId, questionId));
        existing.setAnsweredAt(answeredAt);
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("MockExamHasQuestion não encontrado: id=" + id);
        }
        repository.deleteById(id);
    }

    // A mesma questão não pode aparecer duas vezes no mesmo simulado.
    private void validateNotLinked(Long mockExamId, Long questionId) {
        if (repository.existsByMockExamIdAndQuestionId(mockExamId, questionId)) {
            throw new IllegalArgumentException(
                    "questão já vinculada ao simulado: mockExamId=" + mockExamId
                            + ", questionId=" + questionId);
        }
    }

    // Enquanto a questão não foi respondida, os dois campos ficam nulos juntos.
    private void validateAnswer(Long answerOptionId, LocalDateTime answeredAt) {
        if (answerOptionId == null && answeredAt != null) {
            throw new IllegalArgumentException(
                    "answeredAt não pode ser informado sem answerOptionId");
        }
        if (answerOptionId != null && answeredAt == null) {
            throw new IllegalArgumentException(
                    "answeredAt é obrigatório quando answerOptionId é informado");
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
        if (answerOptionId == null) {
            return null;
        }
        AnswerOption answerOption = answerOptionRepository.findById(answerOptionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "AnswerOption não encontrada: id=" + answerOptionId));
        if (!answerOption.getQuestion().getId().equals(questionId)) {
            throw new IllegalArgumentException(
                    "answerOptionId=" + answerOptionId
                            + " não pertence à questão: questionId=" + questionId);
        }
        return answerOption;
    }
}
