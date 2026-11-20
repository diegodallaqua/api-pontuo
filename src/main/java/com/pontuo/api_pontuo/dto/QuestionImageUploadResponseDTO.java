package com.pontuo.api_pontuo.dto;

import java.time.Instant;
import java.util.Map;

public record QuestionImageUploadResponseDTO(
        String uploadUrl,
        Map<String, String> headers,
        String url,
        Instant expiresAt
) {
}
