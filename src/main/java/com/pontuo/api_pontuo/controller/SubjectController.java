package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.dto.KnowledgeAreaResponseDTO;
import com.pontuo.api_pontuo.dto.SubjectRequestDTO;
import com.pontuo.api_pontuo.dto.SubjectResponseDTO;
import com.pontuo.api_pontuo.entity.KnowledgeArea;
import com.pontuo.api_pontuo.entity.Subject;
import com.pontuo.api_pontuo.service.SubjectService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService service;

    public SubjectController(SubjectService service) {
        this.service = service;
    }

    @GetMapping
    public List<SubjectResponseDTO> findAll(
            @RequestParam(required = false) Long knowledgeAreaId) {
        List<Subject> subjects = knowledgeAreaId == null
                ? service.findAll()
                : service.findByKnowledgeAreaId(knowledgeAreaId);
        return subjects.stream()
                .map(this::toResponseDTO)
                .toList();
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
