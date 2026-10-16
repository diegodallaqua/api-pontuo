package com.pontuo.api_pontuo.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Envelope das listagens paginadas. É um contrato próprio, e não o JSON do
 * {@code Page} do Spring Data, porque o formato daquele não é estável entre
 * versões e o cliente depende dele.
 */
public record PageResponseDTO<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {

    public static <T> PageResponseDTO<T> of(Page<T> page) {
        return new PageResponseDTO<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }
}
