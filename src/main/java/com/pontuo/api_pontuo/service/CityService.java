package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.State;
import com.pontuo.api_pontuo.entity.City;
import com.pontuo.api_pontuo.repository.CityRepository;
import com.pontuo.api_pontuo.repository.StateRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CityService {

    private final CityRepository repository;
    private final StateRepository stateRepository;

    public CityService(CityRepository repository,
                          StateRepository stateRepository) {
        this.repository = repository;
        this.stateRepository = stateRepository;
    }

    public List<City> findAll() {
        return repository.findAll();
    }

    public List<City> findByStateId(Long stateId) {
        if (!stateRepository.existsById(stateId)) {
            throw new EntityNotFoundException(
                    "State não encontrado: id=" + stateId);
        }
        return repository.findByStateId(stateId);
    }

    public Optional<City> findById(Long id) {
        return repository.findById(id);
    }

    public City create(City city, Long stateId) {
        city.setState(findState(stateId));
        return repository.save(city);
    }

    public City update(Long id, City updated, Long stateId) {
        City existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "City não encontrada: id=" + id));
        existing.setName(updated.getName());
        existing.setState(findState(stateId));
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("City não encontrada: id=" + id);
        }
        repository.deleteById(id);
    }

    private State findState(Long stateId) {
        return stateRepository.findById(stateId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "State não encontrado: id=" + stateId));
    }
}
