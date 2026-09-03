package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.KnowledgeArea;
import com.pontuo.api_pontuo.entity.Subject;
import com.pontuo.api_pontuo.repository.KnowledgeAreaRepository;
import com.pontuo.api_pontuo.repository.SubjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {

    private final SubjectRepository repository;
    private final KnowledgeAreaRepository knowledgeAreaRepository;

    public SubjectService(SubjectRepository repository,
                          KnowledgeAreaRepository knowledgeAreaRepository) {
        this.repository = repository;
        this.knowledgeAreaRepository = knowledgeAreaRepository;
    }

    public List<Subject> findAll() {
        return repository.findAll();
    }

    public List<Subject> findByKnowledgeAreaId(Long knowledgeAreaId) {
        if (!knowledgeAreaRepository.existsById(knowledgeAreaId)) {
            throw new EntityNotFoundException(
                    "KnowledgeArea não encontrada: id=" + knowledgeAreaId);
        }
        return repository.findByKnowledgeAreaId(knowledgeAreaId);
    }

    public Optional<Subject> findById(Long id) {
        return repository.findById(id);
    }

    public Subject create(Subject subject, Long knowledgeAreaId) {
        subject.setKnowledgeArea(findKnowledgeArea(knowledgeAreaId));
        return repository.save(subject);
    }

    public Subject update(Long id, Subject updated, Long knowledgeAreaId) {
        Subject existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Subject não encontrada: id=" + id));
        existing.setDescription(updated.getDescription());
        existing.setKnowledgeArea(findKnowledgeArea(knowledgeAreaId));
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Subject não encontrada: id=" + id);
        }
        repository.deleteById(id);
    }

    private KnowledgeArea findKnowledgeArea(Long knowledgeAreaId) {
        return knowledgeAreaRepository.findById(knowledgeAreaId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "KnowledgeArea não encontrada: id=" + knowledgeAreaId));
    }
}
