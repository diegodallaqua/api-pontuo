package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.dto.AddressRequestDTO;
import com.pontuo.api_pontuo.dto.AddressResponseDTO;
import com.pontuo.api_pontuo.dto.CityResponseDTO;
import com.pontuo.api_pontuo.dto.StateResponseDTO;
import com.pontuo.api_pontuo.entity.Address;
import com.pontuo.api_pontuo.entity.City;
import com.pontuo.api_pontuo.entity.State;
import com.pontuo.api_pontuo.service.AddressService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <b>Facade</b> do recurso de endereços: as cinco rotas abaixo escondem do
 * cliente o {@link AddressService}, os repositórios de endereço e de cidade, a
 * validação do corpo da requisição e a conversão entre DTO e entidade. É o
 * controller exemplar do padrão — os demais seguem a mesma forma, descrita em
 * {@code package-info.java}.
 */
@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService service;

    public AddressController(AddressService service) {
        this.service = service;
    }

    @GetMapping
    public List<AddressResponseDTO> findAll(
            @RequestParam(required = false) Long cityId) {
        List<Address> addresses = cityId == null
                ? service.findAll()
                : service.findByCityId(cityId);
        return addresses.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(this::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AddressResponseDTO create(@Valid @RequestBody AddressRequestDTO requestDTO) {
        Address created = service.create(toEntity(requestDTO), requestDTO.cityId());
        return toResponseDTO(created);
    }

    @PutMapping("/{id}")
    public AddressResponseDTO update(@PathVariable Long id,
                                     @Valid @RequestBody AddressRequestDTO requestDTO) {
        Address updated = service.update(id, toEntity(requestDTO), requestDTO.cityId());
        return toResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    private AddressResponseDTO toResponseDTO(Address entity) {
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

    private Address toEntity(AddressRequestDTO dto) {
        return new Address(
                dto.street(),
                dto.neighborhood(),
                dto.number(),
                dto.complement());
    }
}
