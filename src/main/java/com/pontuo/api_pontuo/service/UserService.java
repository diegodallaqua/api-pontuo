package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.User;
import com.pontuo.api_pontuo.entity.UserRole;
import com.pontuo.api_pontuo.repository.UserRepository;
import com.pontuo.api_pontuo.repository.UserRoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    /** Perfil atribuído a quem se cadastra pela rota pública. */
    private static final String DEFAULT_ROLE_DESCRIPTION = "Estudante";

    private final UserRepository repository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository,
                       UserRoleRepository userRoleRepository,
                       PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public List<User> findByUserRoleId(Long userRoleId) {
        if (!userRoleRepository.existsById(userRoleId)) {
            throw new EntityNotFoundException(
                    "UserRole não encontrada: id=" + userRoleId);
        }
        return repository.findByUserRoleId(userRoleId);
    }

    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    public User create(User user, Long userRoleId) {
        if (repository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException(
                    "username já cadastrado: " + user.getUsername());
        }
        if (repository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException(
                    "email já cadastrado: " + user.getEmail());
        }
        user.setUserRole(findUserRole(userRoleId));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }

    /**
     * Cadastro público: a role nunca vem da requisição, é sempre a de estudante.
     * Assim a rota aberta não pode ser usada para criar um administrador.
     */
    public User register(User user) {
        UserRole defaultRole = userRoleRepository.findByDescription(DEFAULT_ROLE_DESCRIPTION)
                .orElseThrow(() -> new IllegalStateException(
                        "UserRole padrão não encontrada: " + DEFAULT_ROLE_DESCRIPTION));
        return create(user, defaultRole.getId());
    }

    public User update(Long id, User updated, Long userRoleId) {
        User existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User não encontrado: id=" + id));

        if (!existing.getUsername().equals(updated.getUsername())
                && repository.existsByUsername(updated.getUsername())) {
            throw new IllegalArgumentException(
                    "username já cadastrado: " + updated.getUsername());
        }
        if (!existing.getEmail().equals(updated.getEmail())
                && repository.existsByEmail(updated.getEmail())) {
            throw new IllegalArgumentException(
                    "email já cadastrado: " + updated.getEmail());
        }

        existing.setUsername(updated.getUsername());
        existing.setEmail(updated.getEmail());
        existing.setBirthDate(updated.getBirthDate());
        existing.setUserRole(findUserRole(userRoleId));
        if (updated.getPassword() != null && !updated.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(updated.getPassword()));
        }
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("User não encontrado: id=" + id);
        }
        repository.deleteById(id);
    }

    private UserRole findUserRole(Long userRoleId) {
        return userRoleRepository.findById(userRoleId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "UserRole não encontrada: id=" + userRoleId));
    }
}
