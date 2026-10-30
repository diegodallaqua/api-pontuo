package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.dto.UserRoleRequestDTO;
import com.pontuo.api_pontuo.dto.UserRoleResponseDTO;
import com.pontuo.api_pontuo.entity.UserRole;
import com.pontuo.api_pontuo.service.UserRoleService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-roles")
public class UserRoleController {

    private final UserRoleService service;

    public UserRoleController(UserRoleService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserRoleResponseDTO> findAll() {
        return service.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public UserRoleResponseDTO findById(@PathVariable @Positive(message = "id deve ser maior que zero") Long id) {
        return service.findById(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException(
                        "UserRole não encontrada: id=" + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserRoleResponseDTO create(@Valid @RequestBody UserRoleRequestDTO requestDTO) {
        UserRole created = service.create(toEntity(requestDTO));
        return toResponseDTO(created);
    }

    @PutMapping("/{id}")
    public UserRoleResponseDTO update(@PathVariable @Positive(message = "id deve ser maior que zero") Long id,
                                            @Valid @RequestBody UserRoleRequestDTO requestDTO) {
        UserRole updated = service.update(id, toEntity(requestDTO));
        return toResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive(message = "id deve ser maior que zero") Long id) {
        service.delete(id);
    }

    private UserRoleResponseDTO toResponseDTO(UserRole entity) {
        return new UserRoleResponseDTO(entity.getId(), entity.getDescription());
    }

    private UserRole toEntity(UserRoleRequestDTO dto) {
        return new UserRole(dto.description());
    }
}