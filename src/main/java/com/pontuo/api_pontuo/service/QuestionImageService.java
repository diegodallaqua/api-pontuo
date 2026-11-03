package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.Question;
import com.pontuo.api_pontuo.entity.QuestionImage;
import com.pontuo.api_pontuo.repository.LikePattern;
import com.pontuo.api_pontuo.repository.QuestionImageRepository;
import com.pontuo.api_pontuo.repository.QuestionRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QuestionImageService {

    private final QuestionImageRepository repository;
    private final QuestionRepository questionRepository;

    public QuestionImageService(QuestionImageRepository repository,
                                QuestionRepository questionRepository) {
        this.repository = repository;
        this.questionRepository = questionRepository;
    }

    public Page<QuestionImage> findAll(String search, Pageable pageable) {
        return repository.search(null, LikePattern.contains(search), pageable);
    }

    public Page<QuestionImage> findByQuestionId(Long questionId, String search, Pageable pageable) {
        if (!questionRepository.existsById(questionId)) {
            throw new EntityNotFoundException("Question não encontrada: id=" + questionId);
        }
        return repository.search(questionId, LikePattern.contains(search), pageable);
    }

    public Optional<QuestionImage> findById(Long id) {
        return repository.findById(id);
    }

    public QuestionImage create(QuestionImage questionImage, Long questionId) {
        questionImage.setQuestion(findQuestion(questionId));
        return repository.save(questionImage);
    }

    public QuestionImage update(Long id, QuestionImage updated, Long questionId) {
        QuestionImage existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "QuestionImage não encontrada: id=" + id));

        existing.setUrl(updated.getUrl());
        existing.setSubtitle(updated.getSubtitle());
        existing.setQuestion(findQuestion(questionId));
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("QuestionImage não encontrada: id=" + id);
        }
        repository.deleteById(id);
    }

    private Question findQuestion(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Question não encontrada: id=" + questionId));
    }
}
