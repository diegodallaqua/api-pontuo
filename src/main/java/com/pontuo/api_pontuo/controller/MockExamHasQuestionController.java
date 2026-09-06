package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.dto.AddressResponseDTO;
import com.pontuo.api_pontuo.dto.AnswerOptionResponseDTO;
import com.pontuo.api_pontuo.dto.CityResponseDTO;
import com.pontuo.api_pontuo.dto.EntranceExamResponseDTO;
import com.pontuo.api_pontuo.dto.InstitutionResponseDTO;
import com.pontuo.api_pontuo.dto.KnowledgeAreaResponseDTO;
import com.pontuo.api_pontuo.dto.MockExamHasQuestionRequestDTO;
import com.pontuo.api_pontuo.dto.MockExamHasQuestionResponseDTO;
import com.pontuo.api_pontuo.dto.MockExamResponseDTO;
import com.pontuo.api_pontuo.dto.QuestionResponseDTO;
import com.pontuo.api_pontuo.dto.StateResponseDTO;
import com.pontuo.api_pontuo.dto.SubjectResponseDTO;
import com.pontuo.api_pontuo.dto.TopicResponseDTO;
import com.pontuo.api_pontuo.dto.UserResponseDTO;
import com.pontuo.api_pontuo.dto.UserRoleResponseDTO;
import com.pontuo.api_pontuo.entity.Address;
import com.pontuo.api_pontuo.entity.AnswerOption;
import com.pontuo.api_pontuo.entity.City;
import com.pontuo.api_pontuo.entity.EntranceExam;
import com.pontuo.api_pontuo.entity.Institution;
import com.pontuo.api_pontuo.entity.KnowledgeArea;
import com.pontuo.api_pontuo.entity.MockExam;
import com.pontuo.api_pontuo.entity.MockExamHasQuestion;
import com.pontuo.api_pontuo.entity.Question;
import com.pontuo.api_pontuo.entity.State;
import com.pontuo.api_pontuo.entity.Subject;
import com.pontuo.api_pontuo.entity.Topic;
import com.pontuo.api_pontuo.entity.User;
import com.pontuo.api_pontuo.entity.UserRole;
import com.pontuo.api_pontuo.service.MockExamHasQuestionService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mock-exam-questions")
public class MockExamHasQuestionController {

    private final MockExamHasQuestionService service;

    public MockExamHasQuestionController(MockExamHasQuestionService service) {
        this.service = service;
    }

    @GetMapping
    public List<MockExamHasQuestionResponseDTO> findAll(
            @RequestParam(required = false) Long mockExamId,
            @RequestParam(required = false) Long questionId) {
        List<MockExamHasQuestion> links;
        if (mockExamId != null) {
            links = service.findByMockExamId(mockExamId);
        } else if (questionId != null) {
            links = service.findByQuestionId(questionId);
        } else {
            links = service.findAll();
        }
        return links.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MockExamHasQuestionResponseDTO> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(this::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MockExamHasQuestionResponseDTO create(
            @Valid @RequestBody MockExamHasQuestionRequestDTO requestDTO) {
        MockExamHasQuestion created = service.create(
                requestDTO.mockExamId(),
                requestDTO.questionId(),
                requestDTO.answerOptionId(),
                requestDTO.answeredAt());
        return toResponseDTO(created);
    }

    @PutMapping("/{id}")
    public MockExamHasQuestionResponseDTO update(
            @PathVariable Long id,
            @Valid @RequestBody MockExamHasQuestionRequestDTO requestDTO) {
        MockExamHasQuestion updated = service.update(
                id,
                requestDTO.mockExamId(),
                requestDTO.questionId(),
                requestDTO.answerOptionId(),
                requestDTO.answeredAt());
        return toResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    private MockExamHasQuestionResponseDTO toResponseDTO(MockExamHasQuestion entity) {
        return new MockExamHasQuestionResponseDTO(
                entity.getId(),
                entity.getAnsweredAt(),
                toMockExamResponseDTO(entity.getMockExam()),
                toQuestionResponseDTO(entity.getQuestion()),
                toAnswerOptionResponseDTO(entity.getAnswerOption()));
    }

    private AnswerOptionResponseDTO toAnswerOptionResponseDTO(AnswerOption entity) {
        if (entity == null) {
            return null;
        }
        return new AnswerOptionResponseDTO(
                entity.getId(),
                entity.getLetter(),
                entity.getText(),
                entity.isRightAnswer(),
                toQuestionResponseDTO(entity.getQuestion()));
    }

    private MockExamResponseDTO toMockExamResponseDTO(MockExam entity) {
        if (entity == null) {
            return null;
        }
        return new MockExamResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getNumQuestions(),
                entity.getMaxTime(),
                entity.getCreatedAt(),
                entity.getStartedAt(),
                entity.getFinishedAt(),
                entity.getCorrectCount(),
                entity.getAccuracy(),
                toUserResponseDTO(entity.getUser()));
    }

    private UserResponseDTO toUserResponseDTO(User entity) {
        if (entity == null) {
            return null;
        }
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
}
