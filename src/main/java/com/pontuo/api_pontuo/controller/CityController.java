package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.dto.CityRequestDTO;
import com.pontuo.api_pontuo.dto.CityResponseDTO;
import com.pontuo.api_pontuo.dto.StateResponseDTO;
import com.pontuo.api_pontuo.entity.City;
import com.pontuo.api_pontuo.entity.State;
import com.pontuo.api_pontuo.service.CityService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    private final CityService service;

    public CityController(CityService service) {
        this.service = service;
    }

    @GetMapping
    public List<CityResponseDTO> findAll(
            @RequestParam(required = false) Long stateId) {
        List<City> cities = stateId == null
                ? service.findAll()
                : service.findByStateId(stateId);
        return cities.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CityResponseDTO> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(this::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CityResponseDTO create(@Valid @RequestBody CityRequestDTO requestDTO) {
        City created = service.create(toEntity(requestDTO), requestDTO.stateId());
        return toResponseDTO(created);
    }

    @PutMapping("/{id}")
    public CityResponseDTO update(@PathVariable Long id,
                                            @Valid @RequestBody CityRequestDTO requestDTO) {
        City updated = service.update(id, toEntity(requestDTO), requestDTO.stateId());
        return toResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    private CityResponseDTO toResponseDTO(City entity) {
        return new CityResponseDTO(
                entity.getId(),
                entity.getName(),
                toStateResponseDTO(entity.getState()));
    }

    private StateResponseDTO toStateResponseDTO(State entity) {
        if (entity == null) {
            return null;
        }
        return new StateResponseDTO(entity.getId(), entity.getName());
    }

    private City toEntity(CityRequestDTO dto) {
        return new City(dto.name());
    }
}
