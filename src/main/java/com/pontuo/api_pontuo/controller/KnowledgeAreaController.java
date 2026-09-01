package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.dto.KnowledgeAreaRequestDTO;
import com.pontuo.api_pontuo.dto.KnowledgeAreaResponseDTO;
import com.pontuo.api_pontuo.entity.KnowledgeArea;
import com.pontuo.api_pontuo.service.KnowledgeAreaService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge-areas")
public class KnowledgeAreaController {

    private final KnowledgeAreaService service;

    public KnowledgeAreaController(KnowledgeAreaService service) {
        this.service = service;
    }

    @GetMapping
    public List<KnowledgeAreaResponseDTO> findAll() {
        return service.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<KnowledgeAreaResponseDTO> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(this::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public KnowledgeAreaResponseDTO create(@Valid @RequestBody KnowledgeAreaRequestDTO requestDTO) {
        KnowledgeArea created = service.create(toEntity(requestDTO));
        return toResponseDTO(created);
    }

    @PutMapping("/{id}")
    public KnowledgeAreaResponseDTO update(@PathVariable Long id,
                                            @Valid @RequestBody KnowledgeAreaRequestDTO requestDTO) {
        KnowledgeArea updated = service.update(id, toEntity(requestDTO));
        return toResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    private KnowledgeAreaResponseDTO toResponseDTO(KnowledgeArea entity) {
        return new KnowledgeAreaResponseDTO(entity.getId(), entity.getDescription());
    }

    private KnowledgeArea toEntity(KnowledgeAreaRequestDTO dto) {
        return new KnowledgeArea(dto.description());
    }
}