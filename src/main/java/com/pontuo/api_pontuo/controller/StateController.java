package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.dto.StateRequestDTO;
import com.pontuo.api_pontuo.dto.StateResponseDTO;
import com.pontuo.api_pontuo.entity.State;
import com.pontuo.api_pontuo.service.StateService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/states")
public class StateController {

    private final StateService service;

    public StateController(StateService service) {
        this.service = service;
    }

    @GetMapping
    public List<StateResponseDTO> findAll() {
        return service.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StateResponseDTO> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(this::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StateResponseDTO create(@Valid @RequestBody StateRequestDTO requestDTO) {
        State created = service.create(toEntity(requestDTO));
        return toResponseDTO(created);
    }

    @PutMapping("/{id}")
    public StateResponseDTO update(@PathVariable Long id,
                                            @Valid @RequestBody StateRequestDTO requestDTO) {
        State updated = service.update(id, toEntity(requestDTO));
        return toResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    private StateResponseDTO toResponseDTO(State entity) {
        return new StateResponseDTO(entity.getId(), entity.getName());
    }

    private State toEntity(StateRequestDTO dto) {
        return new State(dto.name());
    }
}