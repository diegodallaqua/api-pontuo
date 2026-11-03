package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.AnswerOption;
import com.pontuo.api_pontuo.entity.Question;
import com.pontuo.api_pontuo.repository.AnswerOptionRepository;
import com.pontuo.api_pontuo.repository.LikePattern;
import com.pontuo.api_pontuo.repository.QuestionRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AnswerOptionService {

    private final AnswerOptionRepository repository;
    private final QuestionRepository questionRepository;

    public AnswerOptionService(AnswerOptionRepository repository,
                               QuestionRepository questionRepository) {
        this.repository = repository;
        this.questionRepository = questionRepository;
    }

    public Page<AnswerOption> findAll(String search, Pageable pageable) {
        return repository.search(null, LikePattern.contains(search), pageable);
    }

    public Page<AnswerOption> findByQuestionId(Long questionId, String search, Pageable pageable) {
        if (!questionRepository.existsById(questionId)) {
            throw new EntityNotFoundException("Question não encontrada: id=" + questionId);
        }
        return repository.search(questionId, LikePattern.contains(search), pageable);
    }

    public Optional<AnswerOption> findById(Long id) {
        return repository.findById(id);
    }

    public AnswerOption create(AnswerOption answerOption, Long questionId) {
        validateLetterFree(questionId, answerOption.getLetter());
        if (answerOption.isRightAnswer()) {
            validateNoRightAnswerYet(questionId);
        }
        answerOption.setQuestion(findQuestion(questionId));
        return repository.save(answerOption);
    }

    public AnswerOption update(Long id, AnswerOption updated, Long questionId) {
        AnswerOption existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "AnswerOption não encontrada: id=" + id));

        boolean sameLetter = existing.getQuestion().getId().equals(questionId)
                && existing.getLetter().equals(updated.getLetter());
        if (!sameLetter) {
            validateLetterFree(questionId, updated.getLetter());
        }
        // Só checa a alternativa correta quando esta passa a ser a correta e
        // ainda não era; caso contrário ela colidiria consigo mesma.
        if (updated.isRightAnswer()
                && !(existing.isRightAnswer() && existing.getQuestion().getId().equals(questionId))) {
            validateNoRightAnswerYet(questionId);
        }

        existing.setLetter(updated.getLetter());
        existing.setText(updated.getText());
        existing.setRightAnswer(updated.isRightAnswer());
        existing.setQuestion(findQuestion(questionId));
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("AnswerOption não encontrada: id=" + id);
        }
        repository.deleteById(id);
    }

    private void validateLetterFree(Long questionId, String letter) {
        if (repository.existsByQuestionIdAndLetter(questionId, letter)) {
            throw new IllegalArgumentException(
                    "letra já usada nesta questão: questionId=" + questionId
                            + ", letter=" + letter);
        }
    }

    private void validateNoRightAnswerYet(Long questionId) {
        if (repository.existsByQuestionIdAndRightAnswerTrue(questionId)) {
            throw new IllegalArgumentException(
                    "questão já tem alternativa correta: questionId=" + questionId);
        }
    }

    private Question findQuestion(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Question não encontrada: id=" + questionId));
    }
}
