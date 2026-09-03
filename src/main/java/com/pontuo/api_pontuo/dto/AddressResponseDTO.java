package com.pontuo.api_pontuo.dto;

public record AddressResponseDTO(
        Long id,
        String street,
        String neighborhood,
        int number, 
        String complement,
        CityResponseDTO city
) {
}