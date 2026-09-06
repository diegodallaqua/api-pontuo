package com.pontuo.api_pontuo.dto;

import java.time.LocalDateTime;

public record QuestionResponseDTO(
        Long id,
        String statement,
        String explanation,
        Short difficulty,
        LocalDateTime createdAt,
        EntranceExamResponseDTO entranceExam,
        TopicResponseDTO topic,
        SubjectResponseDTO subject
) {
}
