package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.UserRole;
import com.pontuo.api_pontuo.repository.UserRoleRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserRoleService {

    private final UserRoleRepository repository;

    public UserRoleService(UserRoleRepository repository) {
        this.repository = repository;
    }

    public List<UserRole> findAll() {
        return repository.findAll();
    }

    public Optional<UserRole> findById(Long id) {
        return repository.findById(id);
    }

    public UserRole create(UserRole UserRole) {
        return repository.save(UserRole);
    }

    public UserRole update(Long id, UserRole updated) {
        UserRole existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "UserRole não encontrada: id=" + id));
        existing.setDescription(updated.getDescription());
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("UserRole não encontrada: id=" + id);
        }
        repository.deleteById(id);
    }
}