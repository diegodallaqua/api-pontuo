package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.dto.AddressResponseDTO;
import com.pontuo.api_pontuo.dto.CityResponseDTO;
import com.pontuo.api_pontuo.dto.EntranceExamResponseDTO;
import com.pontuo.api_pontuo.dto.InstitutionResponseDTO;
import com.pontuo.api_pontuo.dto.KnowledgeAreaResponseDTO;
import com.pontuo.api_pontuo.dto.QuestionImageRequestDTO;
import com.pontuo.api_pontuo.dto.QuestionImageResponseDTO;
import com.pontuo.api_pontuo.dto.QuestionResponseDTO;
import com.pontuo.api_pontuo.dto.StateResponseDTO;
import com.pontuo.api_pontuo.dto.SubjectResponseDTO;
import com.pontuo.api_pontuo.dto.TopicResponseDTO;
import com.pontuo.api_pontuo.entity.Address;
import com.pontuo.api_pontuo.entity.City;
import com.pontuo.api_pontuo.entity.EntranceExam;
import com.pontuo.api_pontuo.entity.Institution;
import com.pontuo.api_pontuo.entity.KnowledgeArea;
import com.pontuo.api_pontuo.entity.Question;
import com.pontuo.api_pontuo.entity.QuestionImage;
import com.pontuo.api_pontuo.entity.State;
import com.pontuo.api_pontuo.entity.Subject;
import com.pontuo.api_pontuo.entity.Topic;
import com.pontuo.api_pontuo.security.Roles;
import com.pontuo.api_pontuo.service.QuestionImageService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/question-images")
public class QuestionImageController {

    private final QuestionImageService service;

    public QuestionImageController(QuestionImageService service) {
        this.service = service;
    }

    @GetMapping
    public List<QuestionImageResponseDTO> findAll(
            @RequestParam(required = false) Long questionId) {
        List<QuestionImage> questionImages = questionId == null
                ? service.findAll()
                : service.findByQuestionId(questionId);
        return questionImages.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public QuestionImageResponseDTO findById(@PathVariable @Positive(message = "id deve ser maior que zero") Long id) {
        return service.findById(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException(
                        "QuestionImage não encontrada: id=" + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionImageResponseDTO create(@Valid @RequestBody QuestionImageRequestDTO requestDTO) {
        QuestionImage created = service.create(toEntity(requestDTO), requestDTO.questionId());
        return toResponseDTO(created);
    }

    @PutMapping("/{id}")
    public QuestionImageResponseDTO update(@PathVariable @Positive(message = "id deve ser maior que zero") Long id,
                                           @Valid @RequestBody QuestionImageRequestDTO requestDTO) {
        QuestionImage updated = service.update(id, toEntity(requestDTO), requestDTO.questionId());
        return toResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive(message = "id deve ser maior que zero") Long id) {
        service.delete(id);
    }

    private QuestionImageResponseDTO toResponseDTO(QuestionImage entity) {
        return new QuestionImageResponseDTO(
                entity.getId(),
                entity.getUrl(),
                entity.getSubtitle(),
                toQuestionResponseDTO(entity.getQuestion()));
    }

    private QuestionResponseDTO toQuestionResponseDTO(Question entity) {
        if (entity == null) {
            return null;
        }
        return new QuestionResponseDTO(
                entity.getId(),
                entity.getStatement(),
                Roles.isAdministrador() ? entity.getExplanation() : null,
                entity.getDifficulty(),
                entity.getCreatedAt(),
                toEntranceExamResponseDTO(entity.getEntranceExam()),
                toTopicResponseDTO(entity.getTopic()),
                toSubjectResponseDTO(entity.getSubject()));
    }

    private EntranceExamResponseDTO toEntranceExamResponseDTO(EntranceExam entity) {
        if (entity == null) {
            return null;
        }
        return new EntranceExamResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getYear(),
                entity.getStage(),
                toInstitutionResponseDTO(entity.getInstitution()));
    }

    private InstitutionResponseDTO toInstitutionResponseDTO(Institution entity) {
        if (entity == null) {
            return null;
        }
        return new InstitutionResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getAcronym(),
                toAddressResponseDTO(entity.getAddress()));
    }

    private AddressResponseDTO toAddressResponseDTO(Address entity) {
        if (entity == null) {
            return null;
        }
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
        return new CityResponseDTO(entity.getId(), entity.getName(), toStateResponseDTO(entity.getState()));
    }

    private StateResponseDTO toStateResponseDTO(State entity) {
        if (entity == null) {
            return null;
        }
        return new StateResponseDTO(entity.getId(), entity.getName());
    }

    private TopicResponseDTO toTopicResponseDTO(Topic entity) {
        if (entity == null) {
            return null;
        }
        return new TopicResponseDTO(
                entity.getId(),
                entity.getDescription(),
                toSubjectResponseDTO(entity.getSubject()));
    }

    private SubjectResponseDTO toSubjectResponseDTO(Subject entity) {
        if (entity == null) {
            return null;
        }
        return new SubjectResponseDTO(
                entity.getId(),
                entity.getDescription(),
                toKnowledgeAreaResponseDTO(entity.getKnowledgeArea()));
    }

    private KnowledgeAreaResponseDTO toKnowledgeAreaResponseDTO(KnowledgeArea entity) {
        if (entity == null) {
            return null;
        }
        return new KnowledgeAreaResponseDTO(entity.getId(), entity.getDescription());
    }

    private QuestionImage toEntity(QuestionImageRequestDTO dto) {
        return new QuestionImage(dto.url(), dto.subtitle());
    }
}
