package com.pontuo.api_pontuo.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MockExamDeadlineTest {

    @Test
    void prazoTerminaExatamenteEmStartedAtMaisMaxTime() {
        MockExam exam = new MockExam("Simulado", (short) 1, (short) 30);
        LocalDateTime startedAt = LocalDateTime.of(2026, 10, 23, 14, 0);
        assertFalse(exam.isExpiredAt(startedAt.plusHours(1)));

        exam.setStartedAt(startedAt);
        assertFalse(exam.isExpiredAt(startedAt.plusMinutes(30).minusNanos(1)));
        assertTrue(exam.isExpiredAt(startedAt.plusMinutes(30)));
    }
}
