package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.dto.CityRequestDTO;
import com.pontuo.api_pontuo.dto.CityResponseDTO;
import com.pontuo.api_pontuo.dto.PageResponseDTO;
import com.pontuo.api_pontuo.dto.StateResponseDTO;
import com.pontuo.api_pontuo.entity.City;
import com.pontuo.api_pontuo.entity.State;
import com.pontuo.api_pontuo.service.CityService;
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
@RequestMapping("/api/cities")
public class CityController {

    private final CityService service;

    public CityController(CityService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponseDTO<CityResponseDTO> findAll(
            @RequestParam(required = false) Long stateId,
            @RequestParam(required = false)
            @Size(max = Search.MAX_LENGTH, message = Search.MESSAGE) String search,
            @RequestParam(defaultValue = Pagination.DEFAULT_PAGE)
            @Min(value = 1, message = Pagination.PAGE_MESSAGE) int page,
            @RequestParam(defaultValue = Pagination.DEFAULT_LIMIT)
            @Min(value = 1, message = Pagination.LIMIT_MESSAGE)
            @Max(value = Pagination.MAX_LIMIT, message = Pagination.LIMIT_MESSAGE) int limit) {
        PageRequest pageRequest = Pagination.of(page, limit);
        Page<City> cities = stateId == null
                ? service.findAll(search, pageRequest)
                : service.findByStateId(stateId, search, pageRequest);
        return PageResponseDTO.of(cities.map(this::toResponseDTO));
    }

    @GetMapping("/{id}")
    public CityResponseDTO findById(@PathVariable @Positive(message = "id deve ser maior que zero") Long id) {
        return service.findById(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException(
                        "City não encontrada: id=" + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CityResponseDTO create(@Valid @RequestBody CityRequestDTO requestDTO) {
        City created = service.create(toEntity(requestDTO), requestDTO.stateId());
        return toResponseDTO(created);
    }

    @PutMapping("/{id}")
    public CityResponseDTO update(@PathVariable @Positive(message = "id deve ser maior que zero") Long id,
                                            @Valid @RequestBody CityRequestDTO requestDTO) {
        City updated = service.update(id, toEntity(requestDTO), requestDTO.stateId());
        return toResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive(message = "id deve ser maior que zero") Long id) {
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
