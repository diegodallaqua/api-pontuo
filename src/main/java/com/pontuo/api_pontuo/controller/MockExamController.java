package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.dto.MockExamRequestDTO;
import com.pontuo.api_pontuo.dto.MockExamResponseDTO;
import com.pontuo.api_pontuo.dto.MockExamResultResponseDTO;
import com.pontuo.api_pontuo.dto.PageResponseDTO;
import com.pontuo.api_pontuo.dto.UserResponseDTO;
import com.pontuo.api_pontuo.dto.UserRoleResponseDTO;
import com.pontuo.api_pontuo.entity.MockExam;
import com.pontuo.api_pontuo.entity.User;
import com.pontuo.api_pontuo.entity.UserRole;
import com.pontuo.api_pontuo.service.MockExamService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mock-exams")
public class MockExamController {

    private final MockExamService service;

    public MockExamController(MockExamService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponseDTO<MockExamResponseDTO> findAll(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false)
            @Size(max = Search.MAX_LENGTH, message = Search.MESSAGE) String search,
            @RequestParam(defaultValue = Pagination.DEFAULT_PAGE)
            @Min(value = 1, message = Pagination.PAGE_MESSAGE) int page,
            @RequestParam(defaultValue = Pagination.DEFAULT_LIMIT)
            @Min(value = 1, message = Pagination.LIMIT_MESSAGE)
            @Max(value = Pagination.MAX_LIMIT, message = Pagination.LIMIT_MESSAGE) int limit) {
        PageRequest pageRequest = Pagination.of(page, limit);
        Page<MockExam> mockExams = userId == null
                ? service.findAll(search, pageRequest)
                : service.findByUserId(userId, search, pageRequest);
        return PageResponseDTO.of(mockExams.map(this::toResponseDTO));
    }

    @GetMapping("/{id}")
    public MockExamResponseDTO findById(@PathVariable @Positive(message = "id deve ser maior que zero") Long id) {
        return service.findById(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException(
                        "MockExam não encontrado: id=" + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MockExamResponseDTO create(@Valid @RequestBody MockExamRequestDTO requestDTO) {
        MockExam created = service.create(toEntity(requestDTO), requestDTO.userId());
        return toResponseDTO(created);
    }

    @PutMapping("/{id}")
    public MockExamResponseDTO update(@PathVariable @Positive(message = "id deve ser maior que zero") Long id,
                                      @Valid @RequestBody MockExamRequestDTO requestDTO) {
        MockExam updated = service.update(id, toEntity(requestDTO), requestDTO.userId());
        return toResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive(message = "id deve ser maior que zero") Long id) {
        service.delete(id);
    }

    /** Registra o início. A partir daqui as questões podem ser respondidas. */
    @PostMapping("/{id}/start")
    public MockExamResponseDTO start(@PathVariable @Positive(message = "id deve ser maior que zero") Long id) {
        return toResponseDTO(service.start(id));
    }

    /** Corrige, trava o simulado e devolve o boletim. */
    @PostMapping("/{id}/finish")
    public MockExamResultResponseDTO finish(@PathVariable @Positive(message = "id deve ser maior que zero") Long id) {
        MockExam finished = service.finish(id);
        return new MockExamResultResponseDTO(toResponseDTO(finished), service.findSubjectResults(id));
    }

    /** Boletim do simulado; a lista de matérias fica vazia até a finalização. */
    @GetMapping("/{id}/result")
    public MockExamResultResponseDTO result(@PathVariable @Positive(message = "id deve ser maior que zero") Long id) {
        return service.findById(id)
                .map(mockExam -> new MockExamResultResponseDTO(
                        toResponseDTO(mockExam), service.findSubjectResults(id)))
                .orElseThrow(() -> new EntityNotFoundException(
                        "MockExam não encontrado: id=" + id));
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
                entity.getAnsweredCount(),
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
        return new MockExam(
                dto.name(),
                dto.numQuestions(),
                dto.maxTime());
    }
}
