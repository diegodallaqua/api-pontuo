package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.dto.AddressResponseDTO;
import com.pontuo.api_pontuo.dto.CityResponseDTO;
import com.pontuo.api_pontuo.dto.EntranceExamRequestDTO;
import com.pontuo.api_pontuo.dto.EntranceExamResponseDTO;
import com.pontuo.api_pontuo.dto.InstitutionResponseDTO;
import com.pontuo.api_pontuo.dto.PageResponseDTO;
import com.pontuo.api_pontuo.dto.StateResponseDTO;
import com.pontuo.api_pontuo.entity.Address;
import com.pontuo.api_pontuo.entity.City;
import com.pontuo.api_pontuo.entity.EntranceExam;
import com.pontuo.api_pontuo.entity.Institution;
import com.pontuo.api_pontuo.entity.State;
import com.pontuo.api_pontuo.service.EntranceExamService;

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
@RequestMapping("/api/entrance-exams")
public class EntranceExamController {

    private final EntranceExamService service;

    public EntranceExamController(EntranceExamService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponseDTO<EntranceExamResponseDTO> findAll(
            @RequestParam(required = false) Long institutionId,
            @RequestParam(required = false) Short year,
            @RequestParam(required = false)
            @Size(max = Search.MAX_LENGTH, message = Search.MESSAGE) String search,
            @RequestParam(defaultValue = Pagination.DEFAULT_PAGE)
            @Min(value = 1, message = Pagination.PAGE_MESSAGE) int page,
            @RequestParam(defaultValue = Pagination.DEFAULT_LIMIT)
            @Min(value = 1, message = Pagination.LIMIT_MESSAGE)
            @Max(value = Pagination.MAX_LIMIT, message = Pagination.LIMIT_MESSAGE) int limit) {
        PageRequest pageRequest = Pagination.of(page, limit);
        Page<EntranceExam> entranceExams;
        if (institutionId != null) {
            entranceExams = service.findByInstitutionId(institutionId, search, pageRequest);
        } else if (year != null) {
            entranceExams = service.findByYear(year, search, pageRequest);
        } else {
            entranceExams = service.findAll(search, pageRequest);
        }
        return PageResponseDTO.of(entranceExams.map(this::toResponseDTO));
    }

    @GetMapping("/{id}")
    public EntranceExamResponseDTO findById(@PathVariable @Positive(message = "id deve ser maior que zero") Long id) {
        return service.findById(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException(
                        "EntranceExam não encontrado: id=" + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntranceExamResponseDTO create(@Valid @RequestBody EntranceExamRequestDTO requestDTO) {
        EntranceExam created = service.create(toEntity(requestDTO), requestDTO.institutionId());
        return toResponseDTO(created);
    }

    @PutMapping("/{id}")
    public EntranceExamResponseDTO update(@PathVariable @Positive(message = "id deve ser maior que zero") Long id,
                                     @Valid @RequestBody EntranceExamRequestDTO requestDTO) {
        EntranceExam updated = service.update(id, toEntity(requestDTO), requestDTO.institutionId());
        return toResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive(message = "id deve ser maior que zero") Long id) {
        service.delete(id);
    }

    private EntranceExamResponseDTO toResponseDTO(EntranceExam entity) {
        return new EntranceExamResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getYear(),
                entity.getStage(),
                toInstitutionResponseDTO(entity.getInstitution()));
    }

    private InstitutionResponseDTO toInstitutionResponseDTO(Institution entity) {
        if (entity == null) {
            return null;
        }
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

    private EntranceExam toEntity(EntranceExamRequestDTO dto) {
        return new EntranceExam(
                dto.name(),
                dto.year(),
                dto.stage());
    }
}
