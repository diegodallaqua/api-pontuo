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
    private final ImageStorageService storage;

    public QuestionImageService(QuestionImageRepository repository,
                                QuestionRepository questionRepository,
                                ImageStorageService storage) {
        this.repository = repository;
        this.questionRepository = questionRepository;
        this.storage = storage;
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

        String previousUrl = existing.getUrl();
        existing.setUrl(updated.getUrl());
        existing.setSubtitle(updated.getSubtitle());
        existing.setQuestion(findQuestion(questionId));
        QuestionImage saved = repository.save(existing);

        // Trocar o arquivo deixaria o anterior no bucket sem ninguém apontando para ele.
        if (!previousUrl.equals(saved.getUrl())) {
            deleteFileIfUnused(previousUrl);
        }
        return saved;
    }

    public void delete(Long id) {
        QuestionImage existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "QuestionImage não encontrada: id=" + id));
        repository.delete(existing);
        deleteFileIfUnused(existing.getUrl());
    }

    // Nada impede cadastrar a mesma URL duas vezes; o arquivo só sai com a última.
    private void deleteFileIfUnused(String url) {
        if (!repository.existsByUrl(url)) {
            storage.delete(url);
        }
    }

    private Question findQuestion(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Question não encontrada: id=" + questionId));
    }
}
