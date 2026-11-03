package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.dto.PageResponseDTO;
import com.pontuo.api_pontuo.dto.UserRequestDTO;
import com.pontuo.api_pontuo.dto.UserResponseDTO;
import com.pontuo.api_pontuo.dto.UserRoleResponseDTO;
import com.pontuo.api_pontuo.entity.User;
import com.pontuo.api_pontuo.entity.UserRole;
import com.pontuo.api_pontuo.service.UserService;
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
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponseDTO<UserResponseDTO> findAll(
            @RequestParam(required = false) Long userRoleId,
            @RequestParam(required = false)
            @Size(max = Search.MAX_LENGTH, message = Search.MESSAGE) String search,
            @RequestParam(defaultValue = Pagination.DEFAULT_PAGE)
            @Min(value = 1, message = Pagination.PAGE_MESSAGE) int page,
            @RequestParam(defaultValue = Pagination.DEFAULT_LIMIT)
            @Min(value = 1, message = Pagination.LIMIT_MESSAGE)
            @Max(value = Pagination.MAX_LIMIT, message = Pagination.LIMIT_MESSAGE) int limit) {
        PageRequest pageRequest = Pagination.of(page, limit);
        Page<User> users = userRoleId == null
                ? service.findAll(search, pageRequest)
                : service.findByUserRoleId(userRoleId, search, pageRequest);
        return PageResponseDTO.of(users.map(this::toResponseDTO));
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
