package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.dto.KnowledgeAreaResponseDTO;
import com.pontuo.api_pontuo.dto.SubjectResponseDTO;
import com.pontuo.api_pontuo.dto.TopicRequestDTO;
import com.pontuo.api_pontuo.dto.TopicResponseDTO;
import com.pontuo.api_pontuo.entity.KnowledgeArea;
import com.pontuo.api_pontuo.entity.Subject;
import com.pontuo.api_pontuo.entity.Topic;

import com.pontuo.api_pontuo.service.TopicService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

    private final TopicService service;

    public TopicController(TopicService service) {
        this.service = service;
    }

    @GetMapping
    public List<TopicResponseDTO> findAll(
            @RequestParam(required = false) Long subjectId) {
        List<Topic> topics = subjectId == null
                ? service.findAll()
                : service.findBySubjectId(subjectId);
        return topics.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public TopicResponseDTO findById(@PathVariable @Positive(message = "id deve ser maior que zero") Long id) {
        return service.findById(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Topic não encontrada: id=" + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TopicResponseDTO create(@Valid @RequestBody TopicRequestDTO requestDTO) {
        Topic created = service.create(toEntity(requestDTO), requestDTO.subjectId());
        return toResponseDTO(created);
    }

    @PutMapping("/{id}")
    public TopicResponseDTO update(@PathVariable @Positive(message = "id deve ser maior que zero") Long id,
                                            @Valid @RequestBody TopicRequestDTO requestDTO) {
        Topic updated = service.update(id, toEntity(requestDTO), requestDTO.subjectId());
        return toResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive(message = "id deve ser maior que zero") Long id) {
        service.delete(id);
    }

    private TopicResponseDTO toResponseDTO(Topic entity) {
        return new TopicResponseDTO(
                entity.getId(),
                entity.getDescription(),
                toSubjectResponseDTO(entity.getSubject()));
    }

    private SubjectResponseDTO toSubjectResponseDTO(Subject entity) {
        if (entity == null) {
            return null;
        }
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

    private Topic toEntity(TopicRequestDTO dto) {
        return new Topic(dto.description());
    }
}