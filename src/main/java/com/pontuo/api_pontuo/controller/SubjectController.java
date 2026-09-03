package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.dto.KnowledgeAreaResponseDTO;
import com.pontuo.api_pontuo.dto.SubjectRequestDTO;
import com.pontuo.api_pontuo.dto.SubjectResponseDTO;
import com.pontuo.api_pontuo.entity.KnowledgeArea;
import com.pontuo.api_pontuo.entity.Subject;
import com.pontuo.api_pontuo.service.SubjectService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<SubjectResponseDTO> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(this::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubjectResponseDTO create(@Valid @RequestBody SubjectRequestDTO requestDTO) {
        Subject created = service.create(toEntity(requestDTO), requestDTO.knowledgeAreaId());
        return toResponseDTO(created);
    }

    @PutMapping("/{id}")
    public SubjectResponseDTO update(@PathVariable Long id,
                                            @Valid @RequestBody SubjectRequestDTO requestDTO) {
        Subject updated = service.update(id, toEntity(requestDTO), requestDTO.knowledgeAreaId());
        return toResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
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
