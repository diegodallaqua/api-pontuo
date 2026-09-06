package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.MockExam;
import com.pontuo.api_pontuo.entity.User;
import com.pontuo.api_pontuo.repository.MockExamRepository;
import com.pontuo.api_pontuo.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MockExamService {

    private final MockExamRepository repository;
    private final UserRepository userRepository;

    public MockExamService(MockExamRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public List<MockExam> findAll() {
        return repository.findAll();
    }

    public List<MockExam> findByUser(User user) {
        return repository.findByUser(user);
    }

    public List<MockExam> findByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User não encontrado: id=" + userId);
        }
        return repository.findByUserId(userId);
    }

    public Optional<MockExam> findById(Long id) {
        return repository.findById(id);
    }

    public MockExam create(MockExam mockExam, Long userId) {
        validatePeriod(mockExam);
        validateCorrectCount(mockExam);
        mockExam.setUser(findUser(userId));
        return repository.save(mockExam);
    }

    public MockExam update(Long id, MockExam updated, Long userId) {
        MockExam existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "MockExam não encontrado: id=" + id));

        validatePeriod(updated);
        validateCorrectCount(updated);

        existing.setName(updated.getName());
        existing.setNumQuestions(updated.getNumQuestions());
        existing.setMaxTime(updated.getMaxTime());
        existing.setStartedAt(updated.getStartedAt());
        existing.setFinishedAt(updated.getFinishedAt());
        existing.setCorrectCount(updated.getCorrectCount());
        existing.setAccuracy(updated.getAccuracy());
        existing.setUser(findUser(userId));
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("MockExam não encontrado: id=" + id);
        }
        repository.deleteById(id);
    }

    private void validatePeriod(MockExam mockExam) {
        if (mockExam.getStartedAt() == null && mockExam.getFinishedAt() != null) {
            throw new IllegalArgumentException(
                    "finishedAt não pode ser informado sem startedAt");
        }
        if (mockExam.getStartedAt() != null
                && mockExam.getFinishedAt() != null
                && mockExam.getFinishedAt().isBefore(mockExam.getStartedAt())) {
            throw new IllegalArgumentException(
                    "finishedAt não pode ser anterior a startedAt");
        }
    }

    private void validateCorrectCount(MockExam mockExam) {
        if (mockExam.getCorrectCount() > mockExam.getNumQuestions()) {
            throw new IllegalArgumentException(
                    "correctCount não pode ser maior que numQuestions");
        }
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User não encontrado: id=" + userId));
    }
}
