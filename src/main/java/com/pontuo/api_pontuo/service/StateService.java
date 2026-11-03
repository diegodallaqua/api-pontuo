package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.State;
import com.pontuo.api_pontuo.repository.LikePattern;
import com.pontuo.api_pontuo.repository.StateRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StateService {

    private final StateRepository repository;

    public StateService(StateRepository repository) {
        this.repository = repository;
    }

    public Page<State> findAll(String search, Pageable pageable) {
        return repository.search(LikePattern.contains(search), pageable);
    }

    public Optional<State> findById(Long id) {
        return repository.findById(id);
    }

    public State create(State state) {
        return repository.save(state);
    }

    public State update(Long id, State updated) {
        State existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "State não encontrada: id=" + id));
        existing.setName(updated.getName());
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("State não encontrada: id=" + id);
        }
        repository.deleteById(id);
    }
}