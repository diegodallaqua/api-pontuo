package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.dto.KnowledgeAreaResponseDTO;
import com.pontuo.api_pontuo.dto.PageResponseDTO;
import com.pontuo.api_pontuo.dto.SubjectRequestDTO;
import com.pontuo.api_pontuo.dto.SubjectResponseDTO;
import com.pontuo.api_pontuo.entity.KnowledgeArea;
import com.pontuo.api_pontuo.entity.Subject;
import com.pontuo.api_pontuo.service.SubjectService;
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
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService service;

    public SubjectController(SubjectService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponseDTO<SubjectResponseDTO> findAll(
            @RequestParam(required = false) Long knowledgeAreaId,
            @RequestParam(required = false)
            @Size(max = Search.MAX_LENGTH, message = Search.MESSAGE) String search,
            @RequestParam(defaultValue = Pagination.DEFAULT_PAGE)
            @Min(value = 1, message = Pagination.PAGE_MESSAGE) int page,
            @RequestParam(defaultValue = Pagination.DEFAULT_LIMIT)
            @Min(value = 1, message = Pagination.LIMIT_MESSAGE)
            @Max(value = Pagination.MAX_LIMIT, message = Pagination.LIMIT_MESSAGE) int limit) {
        PageRequest pageRequest = Pagination.of(page, limit);
        Page<Subject> subjects = knowledgeAreaId == null
                ? service.findAll(search, pageRequest)
                : service.findByKnowledgeAreaId(knowledgeAreaId, search, pageRequest);
        return PageResponseDTO.of(subjects.map(this::toResponseDTO));
    }

    @GetMapping("/{id}")
    public SubjectResponseDTO findById(@PathVariable @Positive(message = "id deve ser maior que zero") Long id) {
        return service.findById(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Subject não encontrada: id=" + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubjectResponseDTO create(@Valid @RequestBody SubjectRequestDTO requestDTO) {
        Subject created = service.create(toEntity(requestDTO), requestDTO.knowledgeAreaId());
        return toResponseDTO(created);
    }

    @PutMapping("/{id}")
    public SubjectResponseDTO update(@PathVariable @Positive(message = "id deve ser maior que zero") Long id,
                                            @Valid @RequestBody SubjectRequestDTO requestDTO) {
        Subject updated = service.update(id, toEntity(requestDTO), requestDTO.knowledgeAreaId());
        return toResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive(message = "id deve ser maior que zero") Long id) {
        service.delete(id);
    }

    private SubjectResponseDTO toResponseDTO(Subject entity) {
        return new SubjectResponseDTO(
                entity.getId(),
                entity.getDescription(),
                toKnowledgeAreaResponseDTO(entity.getKnowledgeArea()));
    }

    private KnowledgeAreaResponseDTO toKnowledgeAreaResponseDTO(KnowledgeArea entity) {
        if (entity == null) {
            return null;
        }
        return new KnowledgeAreaResponseDTO(entity.getId(), entity.getDescription());
    }

    private Subject toEntity(SubjectRequestDTO dto) {
        return new Subject(dto.description());
    }
}
