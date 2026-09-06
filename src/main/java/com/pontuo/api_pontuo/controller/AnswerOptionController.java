package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.dto.AddressResponseDTO;
import com.pontuo.api_pontuo.dto.AnswerOptionRequestDTO;
import com.pontuo.api_pontuo.dto.AnswerOptionResponseDTO;
import com.pontuo.api_pontuo.dto.CityResponseDTO;
import com.pontuo.api_pontuo.dto.EntranceExamResponseDTO;
import com.pontuo.api_pontuo.dto.InstitutionResponseDTO;
import com.pontuo.api_pontuo.dto.KnowledgeAreaResponseDTO;
import com.pontuo.api_pontuo.dto.QuestionResponseDTO;
import com.pontuo.api_pontuo.dto.StateResponseDTO;
import com.pontuo.api_pontuo.dto.SubjectResponseDTO;
import com.pontuo.api_pontuo.dto.TopicResponseDTO;
import com.pontuo.api_pontuo.entity.Address;
import com.pontuo.api_pontuo.entity.AnswerOption;
import com.pontuo.api_pontuo.entity.City;
import com.pontuo.api_pontuo.entity.EntranceExam;
import com.pontuo.api_pontuo.entity.Institution;
import com.pontuo.api_pontuo.entity.KnowledgeArea;
import com.pontuo.api_pontuo.entity.Question;
import com.pontuo.api_pontuo.entity.State;
import com.pontuo.api_pontuo.entity.Subject;
import com.pontuo.api_pontuo.entity.Topic;
import com.pontuo.api_pontuo.service.AnswerOptionService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/answer-options")
public class AnswerOptionController {

    private final AnswerOptionService service;

    public AnswerOptionController(AnswerOptionService service) {
        this.service = service;
    }

    @GetMapping
    public List<AnswerOptionResponseDTO> findAll(
            @RequestParam(required = false) Long questionId) {
        List<AnswerOption> answerOptions = questionId == null
                ? service.findAll()
                : service.findByQuestionId(questionId);
        return answerOptions.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnswerOptionResponseDTO> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(this::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnswerOptionResponseDTO create(@Valid @RequestBody AnswerOptionRequestDTO requestDTO) {
        AnswerOption created = service.create(toEntity(requestDTO), requestDTO.questionId());
        return toResponseDTO(created);
    }

    @PutMapping("/{id}")
    public AnswerOptionResponseDTO update(@PathVariable Long id,
                                          @Valid @RequestBody AnswerOptionRequestDTO requestDTO) {
        AnswerOption updated = service.update(id, toEntity(requestDTO), requestDTO.questionId());
        return toResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    private AnswerOptionResponseDTO toResponseDTO(AnswerOption entity) {
        return new AnswerOptionResponseDTO(
                entity.getId(),
                entity.getLetter(),
                entity.getText(),
                entity.isRightAnswer(),
                toQuestionResponseDTO(entity.getQuestion()));
    }

    private QuestionResponseDTO toQuestionResponseDTO(Question entity) {
        if (entity == null) {
            return null;
        }
        return new QuestionResponseDTO(
                entity.getId(),
                entity.getStatement(),
                entity.getExplanation(),
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

    private AnswerOption toEntity(AnswerOptionRequestDTO dto) {
        return new AnswerOption(
                dto.letter(),
                dto.text(),
                dto.rightAnswer());
    }
}
