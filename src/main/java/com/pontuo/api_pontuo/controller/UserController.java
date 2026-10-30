package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.dto.UserRequestDTO;
import com.pontuo.api_pontuo.dto.UserResponseDTO;
import com.pontuo.api_pontuo.dto.UserRoleResponseDTO;
import com.pontuo.api_pontuo.entity.User;
import com.pontuo.api_pontuo.entity.UserRole;
import com.pontuo.api_pontuo.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserResponseDTO> findAll(
            @RequestParam(required = false) Long userRoleId) {
        List<User> users = userRoleId == null
                ? service.findAll()
                : service.findByUserRoleId(userRoleId);
        return users.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public UserResponseDTO findById(@PathVariable @Positive(message = "id deve ser maior que zero") Long id) {
        return service.findById(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User não encontrado: id=" + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO create(@Valid @RequestBody UserRequestDTO requestDTO) {
        User created = service.create(toEntity(requestDTO), requestDTO.userRoleId());
        return toResponseDTO(created);
    }

    @PutMapping("/{id}")
    public UserResponseDTO update(@PathVariable @Positive(message = "id deve ser maior que zero") Long id,
                                            @Valid @RequestBody UserRequestDTO requestDTO) {
        User updated = service.update(id, toEntity(requestDTO), requestDTO.userRoleId());
        return toResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive(message = "id deve ser maior que zero") Long id) {
        service.delete(id);
    }

    private UserResponseDTO toResponseDTO(User entity) {
        return new UserResponseDTO(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getBirthDate(),
                entity.getCreatedAt(),
                toUserRoleResponseDTO(entity.getUserRole()));
    }

    private UserRoleResponseDTO toUserRoleResponseDTO(UserRole entity) {
        if (entity == null) {
            return null;
        }
        return new UserRoleResponseDTO(entity.getId(), entity.getDescription());
    }

    private User toEntity(UserRequestDTO dto) {
        return new User(dto.username(), dto.email(), dto.birthDate(), dto.password());
    }
}
