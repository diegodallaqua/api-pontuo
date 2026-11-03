package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.KnowledgeArea;
import com.pontuo.api_pontuo.repository.KnowledgeAreaRepository;
import com.pontuo.api_pontuo.repository.LikePattern;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KnowledgeAreaService {

    private final KnowledgeAreaRepository repository;

    public KnowledgeAreaService(KnowledgeAreaRepository repository) {
        this.repository = repository;
    }

    public Page<KnowledgeArea> findAll(String search, Pageable pageable) {
        return repository.search(LikePattern.contains(search), pageable);
    }

    public Optional<KnowledgeArea> findById(Long id) {
        return repository.findById(id);
    }

    public KnowledgeArea create(KnowledgeArea knowledgeArea) {
        return repository.save(knowledgeArea);
    }

    public KnowledgeArea update(Long id, KnowledgeArea updated) {
        KnowledgeArea existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "KnowledgeArea não encontrada: id=" + id));
        existing.setDescription(updated.getDescription());
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("KnowledgeArea não encontrada: id=" + id);
        }
        repository.deleteById(id);
    }
}