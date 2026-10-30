package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.dto.EvolutionPointResponseDTO;
import com.pontuo.api_pontuo.dto.PerformanceFilterDTO;
import com.pontuo.api_pontuo.dto.PerformanceItemResponseDTO;
import com.pontuo.api_pontuo.dto.PerformanceOverviewResponseDTO;
import com.pontuo.api_pontuo.service.PerformanceService;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Desempenho do estudante nos simulados finalizados. Cada rota existe em duas
 * formas: {@code /me/...}, para o dono do token, e {@code /users/{userId}/...},
 * exclusiva do administrador (bloqueada também no SecurityConfig).
 *
 * <p>Filtros comuns, todos opcionais: {@code from} e {@code to} (datas ISO,
 * inclusivas, sobre a finalização) e {@code entranceExamId}, que vale nos
 * detalhamentos.
 */
@RestController
@RequestMapping("/api/performance")
public class PerformanceController {

    private final PerformanceService service;

    public PerformanceController(PerformanceService service) {
        this.service = service;
    }

    @GetMapping({"/me/overview", "/users/{userId}/overview"})
    public PerformanceOverviewResponseDTO overview(
            @PathVariable(required = false)
            @Positive(message = "userId deve ser maior que zero") Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.overview(userId, new PerformanceFilterDTO(from, to, null));
    }

    @GetMapping({"/me/knowledge-areas", "/users/{userId}/knowledge-areas"})
    public List<PerformanceItemResponseDTO> byKnowledgeArea(
            @PathVariable(required = false)
            @Positive(message = "userId deve ser maior que zero") Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) Long entranceExamId) {
        return service.byKnowledgeArea(userId, new PerformanceFilterDTO(from, to, entranceExamId));
    }

    @GetMapping({"/me/subjects", "/users/{userId}/subjects"})
    public List<PerformanceItemResponseDTO> bySubject(
            @PathVariable(required = false)
            @Positive(message = "userId deve ser maior que zero") Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) Long entranceExamId,
            @RequestParam(required = false) Long knowledgeAreaId) {
        return service.bySubject(userId, new PerformanceFilterDTO(from, to, entranceExamId), knowledgeAreaId);
    }

    @GetMapping({"/me/topics", "/users/{userId}/topics"})
    public List<PerformanceItemResponseDTO> byTopic(
            @PathVariable(required = false)
            @Positive(message = "userId deve ser maior que zero") Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) Long entranceExamId,
            @RequestParam(required = false) Long subjectId) {
        return service.byTopic(userId, new PerformanceFilterDTO(from, to, entranceExamId), subjectId);
    }

    @GetMapping({"/me/difficulty", "/users/{userId}/difficulty"})
    public List<PerformanceItemResponseDTO> byDifficulty(
            @PathVariable(required = false)
            @Positive(message = "userId deve ser maior que zero") Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) Long entranceExamId) {
        return service.byDifficulty(userId, new PerformanceFilterDTO(from, to, entranceExamId));
    }

    @GetMapping({"/me/weak-topics", "/users/{userId}/weak-topics"})
    public List<PerformanceItemResponseDTO> weakTopics(
            @PathVariable(required = false)
            @Positive(message = "userId deve ser maior que zero") Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) Long entranceExamId,
            @RequestParam(defaultValue = "5")
            @Min(value = 1, message = "minQuestions deve ser no mínimo 1") int minQuestions,
            @RequestParam(defaultValue = "5")
            @Min(value = 1, message = "limit deve ser no mínimo 1")
            @Max(value = 50, message = "limit deve ser no máximo 50") int limit) {
        return service.weakTopics(userId, new PerformanceFilterDTO(from, to, entranceExamId), minQuestions, limit);
    }

    @GetMapping({"/me/evolution", "/users/{userId}/evolution"})
    public List<EvolutionPointResponseDTO> evolution(
            @PathVariable(required = false)
            @Positive(message = "userId deve ser maior que zero") Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) Long subjectId) {
        return service.evolution(userId, new PerformanceFilterDTO(from, to, null), subjectId);
    }
}
