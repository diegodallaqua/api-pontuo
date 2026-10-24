package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.dto.AddressResponseDTO;
import com.pontuo.api_pontuo.dto.AnswerOptionSummaryResponseDTO;
import com.pontuo.api_pontuo.dto.CityResponseDTO;
import com.pontuo.api_pontuo.dto.EntranceExamResponseDTO;
import com.pontuo.api_pontuo.dto.InstitutionResponseDTO;
import com.pontuo.api_pontuo.dto.KnowledgeAreaResponseDTO;
import com.pontuo.api_pontuo.dto.PageResponseDTO;
import com.pontuo.api_pontuo.dto.QuestionDrawResponseDTO;
import com.pontuo.api_pontuo.dto.QuestionFilterDTO;
import com.pontuo.api_pontuo.dto.QuestionImageSummaryResponseDTO;
import com.pontuo.api_pontuo.dto.QuestionRequestDTO;
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
import com.pontuo.api_pontuo.entity.QuestionImage;
import com.pontuo.api_pontuo.entity.State;
import com.pontuo.api_pontuo.entity.Subject;
import com.pontuo.api_pontuo.entity.Topic;
import com.pontuo.api_pontuo.security.Roles;
import com.pontuo.api_pontuo.service.DrawnQuestion;
import com.pontuo.api_pontuo.service.QuestionService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    /**
     * Teto do tamanho de página e do sorteio. O catálogo tem milhares de
     * questões: sem teto uma única chamada arrastaria tudo.
     */
    private static final int MAX_SIZE = 100;

    /** Faixa de dificuldade aceita, igual à do cadastro da questão. */
    private static final String DIFFICULTY_RANGE = "difficulty deve estar entre 1 e 3";

    private final QuestionService service;

    public QuestionController(QuestionService service) {
        this.service = service;
    }

    /**
     * Listagem paginada. Os filtros se combinam: mandar vestibular e área
     * junto devolve as questões daquele vestibular naquela área.
     */
    @GetMapping
    public PageResponseDTO<QuestionResponseDTO> findAll(
            @RequestParam(required = false) Long entranceExamId,
            @RequestParam(required = false) Long knowledgeAreaId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false)
            @Min(value = 1, message = DIFFICULTY_RANGE)
            @Max(value = 3, message = DIFFICULTY_RANGE) Short difficulty,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "page não pode ser negativo") int page,
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "size deve estar entre 1 e " + MAX_SIZE)
            @Max(value = MAX_SIZE, message = "size deve estar entre 1 e " + MAX_SIZE) int size) {
        // Ordem fixa por id: sem ordenação definida a mesma questão poderia
        // aparecer em duas páginas, ou em nenhuma.
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        return PageResponseDTO.of(service
                .search(toFilter(entranceExamId, knowledgeAreaId, subjectId, topicId, difficulty),
                        pageRequest)
                .map(this::toResponseDTO));
    }

    /**
     * Sorteia questões para montar um simulado. Devolve a lista já embaralhada,
     * sem paginação, porque o cliente usa todas de uma vez.
     */
    @GetMapping("/draw")
    public List<QuestionDrawResponseDTO> draw(
            @RequestParam(required = false) Long entranceExamId,
            @RequestParam(required = false) Long knowledgeAreaId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false)
            @Min(value = 1, message = DIFFICULTY_RANGE)
            @Max(value = 3, message = DIFFICULTY_RANGE) Short difficulty,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "size deve estar entre 1 e " + MAX_SIZE)
            @Max(value = MAX_SIZE, message = "size deve estar entre 1 e " + MAX_SIZE) int size) {
        return service
                .draw(toFilter(entranceExamId, knowledgeAreaId, subjectId, topicId, difficulty), size)
                .stream()
                .map(this::toDrawResponseDTO)
                .toList();
    }

    private QuestionFilterDTO toFilter(Long entranceExamId, Long knowledgeAreaId,
                                       Long subjectId, Long topicId, Short difficulty) {
        return new QuestionFilterDTO(entranceExamId, knowledgeAreaId, subjectId, topicId, difficulty);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponseDTO> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(this::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionResponseDTO create(@Valid @RequestBody QuestionRequestDTO requestDTO) {
        Question created = service.create(
                toEntity(requestDTO),
                requestDTO.entranceExamId(),
                requestDTO.topicId(),
                requestDTO.subjectId());
        return toResponseDTO(created);
    }

    @PutMapping("/{id}")
    public QuestionResponseDTO update(@PathVariable Long id,
                                      @Valid @RequestBody QuestionRequestDTO requestDTO) {
        Question updated = service.update(
                id,
                toEntity(requestDTO),
                requestDTO.entranceExamId(),
                requestDTO.topicId(),
                requestDTO.subjectId());
        return toResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // Sem explanation de propósito: durante a prova a resolução comentada é
    // gabarito. Ela volta no resultado do simulado, depois de respondido.
    private QuestionDrawResponseDTO toDrawResponseDTO(DrawnQuestion drawn) {
        Question entity = drawn.question();
        return new QuestionDrawResponseDTO(
                entity.getId(),
                entity.getStatement(),
                entity.getDifficulty(),
                entity.getCreatedAt(),
                toEntranceExamResponseDTO(entity.getEntranceExam()),
                toTopicResponseDTO(entity.getTopic()),
                toSubjectResponseDTO(entity.getSubject()),
                drawn.images().stream().map(this::toImageSummaryResponseDTO).toList(),
                drawn.answerOptions().stream().map(this::toOptionSummaryResponseDTO).toList());
    }

    private QuestionImageSummaryResponseDTO toImageSummaryResponseDTO(QuestionImage entity) {
        return new QuestionImageSummaryResponseDTO(
                entity.getId(), entity.getUrl(), entity.getSubtitle());
    }

    // rightAnswer fica de fora: com ele na resposta o estudante leria o
    // gabarito antes de responder.
    private AnswerOptionSummaryResponseDTO toOptionSummaryResponseDTO(AnswerOption entity) {
        return new AnswerOptionSummaryResponseDTO(
                entity.getId(), entity.getLetter(), entity.getText());
    }

    private QuestionResponseDTO toResponseDTO(Question entity) {
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

    private Question toEntity(QuestionRequestDTO dto) {
        return new Question(
                dto.statement(),
                dto.explanation(),
                dto.difficulty());
    }
}
