package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.Topic;
import com.pontuo.api_pontuo.entity.Subject;
import com.pontuo.api_pontuo.repository.SubjectRepository;
import com.pontuo.api_pontuo.repository.TopicRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TopicService {

    private final TopicRepository repository;
    private final SubjectRepository subjectRepository;

    public TopicService(TopicRepository repository,
                          SubjectRepository subjectRepository) {
        this.repository = repository;
        this.subjectRepository = subjectRepository;
    }

    public List<Topic> findAll() {
        return repository.findAll();
    }

    public List<Topic> findBySubjectId(Long subjectId) {
        if (!subjectRepository.existsById(subjectId)) {
            throw new EntityNotFoundException(
                    "Subject não encontrada: id=" + subjectId);
        }
        return repository.findBySubjectId(subjectId);
    }

    public Optional<Topic> findById(Long id) {
        return repository.findById(id);
    }

    public Topic create(Topic topic, Long subjectId) {
        topic.setSubject(findSubject(subjectId));
        return repository.save(topic);
    }

    public Topic update(Long id, Topic updated, Long subjectId) {
        Topic existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Topic não encontrada: id=" + id));
        existing.setDescription(updated.getDescription());
        existing.setSubject(findSubject(subjectId));
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Topic não encontrada: id=" + id);
        }
        repository.deleteById(id);
    }

    private Subject findSubject(Long subjectId) {
        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Subject não encontrada: id=" + subjectId));
    }
}
