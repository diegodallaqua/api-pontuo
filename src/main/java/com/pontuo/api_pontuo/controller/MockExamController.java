package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.dto.MockExamRequestDTO;
import com.pontuo.api_pontuo.dto.MockExamResponseDTO;
import com.pontuo.api_pontuo.dto.UserResponseDTO;
import com.pontuo.api_pontuo.dto.UserRoleResponseDTO;
import com.pontuo.api_pontuo.entity.MockExam;
import com.pontuo.api_pontuo.entity.User;
import com.pontuo.api_pontuo.entity.UserRole;
import com.pontuo.api_pontuo.service.MockExamService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mock-exams")
public class MockExamController {

    private final MockExamService service;

    public MockExamController(MockExamService service) {
        this.service = service;
    }

    @GetMapping
    public List<MockExamResponseDTO> findAll(
            @RequestParam(required = false) Long userId) {
        List<MockExam> mockExams = userId == null
                ? service.findAll()
                : service.findByUserId(userId);
        return mockExams.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MockExamResponseDTO> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(this::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MockExamResponseDTO create(@Valid @RequestBody MockExamRequestDTO requestDTO) {
        MockExam created = service.create(toEntity(requestDTO), requestDTO.userId());
        return toResponseDTO(created);
    }

    @PutMapping("/{id}")
    public MockExamResponseDTO update(@PathVariable Long id,
                                      @Valid @RequestBody MockExamRequestDTO requestDTO) {
        MockExam updated = service.update(id, toEntity(requestDTO), requestDTO.userId());
        return toResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    private MockExamResponseDTO toResponseDTO(MockExam entity) {
        return new MockExamResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getNumQuestions(),
                entity.getMaxTime(),
                entity.getCreatedAt(),
                entity.getStartedAt(),
                entity.getFinishedAt(),
                entity.getCorrectCount(),
                entity.getAccuracy(),
                toUserResponseDTO(entity.getUser()));
    }

    private UserResponseDTO toUserResponseDTO(User entity) {
        if (entity == null) {
            return null;
        }
        return new UserResponseDTO(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getBirthDate(),
                entity.getCreatedAt(),
                toUserRoleResponseDTO(entity.getUserRole()));
    }

    private UserRoleResponseDTO toUserRoleResponseDTO(UserRole entity) {
        if (entity == null) {
            return null;
        }
        return new UserRoleResponseDTO(entity.getId(), entity.getDescription());
    }

    private MockExam toEntity(MockExamRequestDTO dto) {
        MockExam mockExam = new MockExam(
                dto.name(),
                dto.numQuestions(),
                dto.maxTime());
        mockExam.setStartedAt(dto.startedAt());
        mockExam.setFinishedAt(dto.finishedAt());
        mockExam.setCorrectCount(dto.correctCount());
        mockExam.setAccuracy(dto.accuracy());
        return mockExam;
    }
}
