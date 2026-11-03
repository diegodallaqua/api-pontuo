package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.dto.PageResponseDTO;
import com.pontuo.api_pontuo.dto.StateRequestDTO;
import com.pontuo.api_pontuo.dto.StateResponseDTO;
import com.pontuo.api_pontuo.entity.State;
import com.pontuo.api_pontuo.service.StateService;

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
@RequestMapping("/api/states")
public class StateController {

    private final StateService service;

    public StateController(StateService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponseDTO<StateResponseDTO> findAll(
            @RequestParam(required = false)
            @Size(max = Search.MAX_LENGTH, message = Search.MESSAGE) String search,
            @RequestParam(defaultValue = Pagination.DEFAULT_PAGE)
            @Min(value = 1, message = Pagination.PAGE_MESSAGE) int page,
            @RequestParam(defaultValue = Pagination.DEFAULT_LIMIT)
            @Min(value = 1, message = Pagination.LIMIT_MESSAGE)
            @Max(value = Pagination.MAX_LIMIT, message = Pagination.LIMIT_MESSAGE) int limit) {
        PageRequest pageRequest = Pagination.of(page, limit);
        return PageResponseDTO.of(service.findAll(search, pageRequest).map(this::toResponseDTO));
    }

    @GetMapping("/{id}")
    public StateResponseDTO findById(@PathVariable @Positive(message = "id deve ser maior que zero") Long id) {
        return service.findById(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException(
                        "State não encontrada: id=" + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StateResponseDTO create(@Valid @RequestBody StateRequestDTO requestDTO) {
        State created = service.create(toEntity(requestDTO));
        return toResponseDTO(created);
    }

    @PutMapping("/{id}")
    public StateResponseDTO update(@PathVariable @Positive(message = "id deve ser maior que zero") Long id,
                                            @Valid @RequestBody StateRequestDTO requestDTO) {
        State updated = service.update(id, toEntity(requestDTO));
        return toResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive(message = "id deve ser maior que zero") Long id) {
        service.delete(id);
    }

    private StateResponseDTO toResponseDTO(State entity) {
        return new StateResponseDTO(entity.getId(), entity.getName());
    }

    private State toEntity(StateRequestDTO dto) {
        return new State(dto.name());
    }
}