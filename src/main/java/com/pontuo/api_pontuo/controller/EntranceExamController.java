package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.dto.AddressResponseDTO;
import com.pontuo.api_pontuo.dto.CityResponseDTO;
import com.pontuo.api_pontuo.dto.EntranceExamRequestDTO;
import com.pontuo.api_pontuo.dto.EntranceExamResponseDTO;
import com.pontuo.api_pontuo.dto.InstitutionResponseDTO;
import com.pontuo.api_pontuo.dto.StateResponseDTO;
import com.pontuo.api_pontuo.entity.Address;
import com.pontuo.api_pontuo.entity.City;
import com.pontuo.api_pontuo.entity.EntranceExam;
import com.pontuo.api_pontuo.entity.Institution;
import com.pontuo.api_pontuo.entity.State;
import com.pontuo.api_pontuo.service.EntranceExamService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entrance-exams")
public class EntranceExamController {

    private final EntranceExamService service;

    public EntranceExamController(EntranceExamService service) {
        this.service = service;
    }

    @GetMapping
    public List<EntranceExamResponseDTO> findAll(
            @RequestParam(required = false) Long institutionId,
            @RequestParam(required = false) Short year) {
        List<EntranceExam> entranceExams;
        if (institutionId != null) {
            entranceExams = service.findByInstitutionId(institutionId);
        } else if (year != null) {
            entranceExams = service.findByYear(year);
        } else {
            entranceExams = service.findAll();
        }
        return entranceExams.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntranceExamResponseDTO> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(this::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntranceExamResponseDTO create(@Valid @RequestBody EntranceExamRequestDTO requestDTO) {
        EntranceExam created = service.create(toEntity(requestDTO), requestDTO.institutionId());
        return toResponseDTO(created);
    }

    @PutMapping("/{id}")
    public EntranceExamResponseDTO update(@PathVariable Long id,
                                     @Valid @RequestBody EntranceExamRequestDTO requestDTO) {
        EntranceExam updated = service.update(id, toEntity(requestDTO), requestDTO.institutionId());
        return toResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
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
