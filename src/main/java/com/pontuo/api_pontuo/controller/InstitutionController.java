package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.dto.AddressResponseDTO;
import com.pontuo.api_pontuo.dto.CityResponseDTO;
import com.pontuo.api_pontuo.dto.InstitutionRequestDTO;
import com.pontuo.api_pontuo.dto.InstitutionResponseDTO;
import com.pontuo.api_pontuo.dto.PageResponseDTO;
import com.pontuo.api_pontuo.dto.StateResponseDTO;
import com.pontuo.api_pontuo.entity.Address;
import com.pontuo.api_pontuo.entity.City;
import com.pontuo.api_pontuo.entity.Institution;
import com.pontuo.api_pontuo.entity.State;
import com.pontuo.api_pontuo.service.InstitutionService;

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
@RequestMapping("/api/institutions")
public class InstitutionController {

    private final InstitutionService service;

    public InstitutionController(InstitutionService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponseDTO<InstitutionResponseDTO> findAll(
            @RequestParam(required = false) String acronym,
            @RequestParam(required = false)
            @Size(max = Search.MAX_LENGTH, message = Search.MESSAGE) String search,
            @RequestParam(defaultValue = Pagination.DEFAULT_PAGE)
            @Min(value = 1, message = Pagination.PAGE_MESSAGE) int page,
            @RequestParam(defaultValue = Pagination.DEFAULT_LIMIT)
            @Min(value = 1, message = Pagination.LIMIT_MESSAGE)
            @Max(value = Pagination.MAX_LIMIT, message = Pagination.LIMIT_MESSAGE) int limit) {
        PageRequest pageRequest = Pagination.of(page, limit);
        Page<Institution> institutions = acronym == null
                ? service.findAll(search, pageRequest)
                : service.findByAcronym(acronym, search, pageRequest);
        return PageResponseDTO.of(institutions.map(this::toResponseDTO));
    }

    @GetMapping("/{id}")
    public InstitutionResponseDTO findById(@PathVariable @Positive(message = "id deve ser maior que zero") Long id) {
        return service.findById(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Institution não encontrada: id=" + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InstitutionResponseDTO create(@Valid @RequestBody InstitutionRequestDTO requestDTO) {
        Institution created = service.create(toEntity(requestDTO), requestDTO.addressId());
        return toResponseDTO(created);
    }

    @PutMapping("/{id}")
    public InstitutionResponseDTO update(@PathVariable @Positive(message = "id deve ser maior que zero") Long id,
                                     @Valid @RequestBody InstitutionRequestDTO requestDTO) {
        Institution updated = service.update(id, toEntity(requestDTO), requestDTO.addressId());
        return toResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive(message = "id deve ser maior que zero") Long id) {
        service.delete(id);
    }

    private InstitutionResponseDTO toResponseDTO(Institution entity) {
        return new InstitutionResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getAcronym(),
                toAddressResponseDTO(entity.getAddress()));
    }

    private AddressResponseDTO toAddressResponseDTO(Address entity) {
        if (entity == null) {
            return null;
        }
        return new AddressResponseDTO(
                entity.getId(),
                entity.getStreet(),
                entity.getNeighborhood(),
                entity.getNumber(),
                entity.getComplement(),
                toCityResponseDTO(entity.getCity()));
    }

    private CityResponseDTO toCityResponseDTO(City entity) {
        if (entity == null) {
            return null;
        }
        return new CityResponseDTO(entity.getId(), entity.getName(), toStateResponseDTO(entity.getState()));
    }

    private StateResponseDTO toStateResponseDTO(State entity) {
        if (entity == null) {
            return null;
        }
        return new StateResponseDTO(entity.getId(), entity.getName());
    }

    private Institution toEntity(InstitutionRequestDTO dto) {
        return new Institution(
                dto.name(),
                dto.acronym());
    }
}
