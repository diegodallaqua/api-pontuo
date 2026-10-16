package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.dto.QuestionFilterDTO;
import com.pontuo.api_pontuo.entity.AnswerOption;
import com.pontuo.api_pontuo.entity.EntranceExam;
import com.pontuo.api_pontuo.entity.Question;
import com.pontuo.api_pontuo.entity.QuestionImage;
import com.pontuo.api_pontuo.entity.Subject;
import com.pontuo.api_pontuo.entity.Topic;
import com.pontuo.api_pontuo.repository.AnswerOptionRepository;
import com.pontuo.api_pontuo.repository.EntranceExamRepository;
import com.pontuo.api_pontuo.repository.KnowledgeAreaRepository;
import com.pontuo.api_pontuo.repository.QuestionImageRepository;
import com.pontuo.api_pontuo.repository.QuestionRepository;
import com.pontuo.api_pontuo.repository.SubjectRepository;
import com.pontuo.api_pontuo.repository.TopicRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    private final QuestionRepository repository;
    private final EntranceExamRepository entranceExamRepository;
    private final TopicRepository topicRepository;
    private final SubjectRepository subjectRepository;
    private final KnowledgeAreaRepository knowledgeAreaRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final QuestionImageRepository questionImageRepository;

    public QuestionService(QuestionRepository repository,
                           EntranceExamRepository entranceExamRepository,
                           TopicRepository topicRepository,
                           SubjectRepository subjectRepository,
                           KnowledgeAreaRepository knowledgeAreaRepository,
                           AnswerOptionRepository answerOptionRepository,
                           QuestionImageRepository questionImageRepository) {
        this.repository = repository;
        this.entranceExamRepository = entranceExamRepository;
        this.topicRepository = topicRepository;
        this.subjectRepository = subjectRepository;
        this.knowledgeAreaRepository = knowledgeAreaRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.questionImageRepository = questionImageRepository;
    }

    /** Busca paginada; os filtros informados valem juntos. */
    public Page<Question> search(QuestionFilterDTO filter, Pageable pageable) {
        validateFilter(filter);
        return repository.search(
                filter.entranceExamId(),
                filter.knowledgeAreaId(),
                filter.subjectId(),
                filter.topicId(),
                filter.difficulty(),
                pageable);
    }

    /**
     * Sorteia até {@code size} questões entre as que passam nos filtros. É o
     * que monta um simulado: sem isto o cliente teria que baixar o catálogo
     * inteiro e escolher por conta própria.
     *
     * <p>Alternativas e imagens vêm junto porque a prova precisa das duas de
     * imediato: buscá-las depois custaria uma requisição por questão.
     */
    public List<DrawnQuestion> draw(QuestionFilterDTO filter, int size) {
        validateFilter(filter);

        List<Long> ids = new ArrayList<>(repository.searchIds(
                filter.entranceExamId(),
                filter.knowledgeAreaId(),
                filter.subjectId(),
                filter.topicId(),
                filter.difficulty()));
        Collections.shuffle(ids);
        List<Long> drawn = List.copyOf(ids.subList(0, Math.min(size, ids.size())));
        if (drawn.isEmpty()) {
            return List.of();
        }

        // findByIdIn devolve na ordem do banco; a resposta segue a ordem do
        // sorteio para o simulado não sair sempre na mesma sequência.
        Map<Long, Question> byId = repository.findByIdIn(drawn).stream()
                .collect(Collectors.toMap(Question::getId, Function.identity()));
        Map<Long, List<AnswerOption>> optionsByQuestion = answerOptionRepository
                .findByQuestionIdIn(drawn).stream()
                .collect(Collectors.groupingBy(option -> option.getQuestion().getId()));
        Map<Long, List<QuestionImage>> imagesByQuestion = questionImageRepository
                .findByQuestionIdIn(drawn).stream()
                .collect(Collectors.groupingBy(image -> image.getQuestion().getId()));

        return drawn.stream()
                .map(byId::get)
                .filter(Objects::nonNull)
                .map(question -> new DrawnQuestion(
                        question,
                        optionsByQuestion.getOrDefault(question.getId(), List.of()),
                        imagesByQuestion.getOrDefault(question.getId(), List.of())))
                .toList();
    }

    public Optional<Question> findById(Long id) {
        return repository.findById(id);
    }

    public Question create(Question question, Long entranceExamId, Long topicId, Long subjectId) {
        validateTopicOrSubject(topicId, subjectId);
        question.setEntranceExam(findEntranceExam(entranceExamId));
        question.setTopic(findTopic(topicId));
        question.setSubject(findSubject(subjectId));
        return repository.save(question);
    }

    public Question update(Long id, Question updated, Long entranceExamId, Long topicId, Long subjectId) {
        Question existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Question não encontrada: id=" + id));

        validateTopicOrSubject(topicId, subjectId);

        existing.setStatement(updated.getStatement());
        existing.setExplanation(updated.getExplanation());
        existing.setDifficulty(updated.getDifficulty());
        existing.setEntranceExam(findEntranceExam(entranceExamId));
        existing.setTopic(findTopic(topicId));
        existing.setSubject(findSubject(subjectId));
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Question não encontrada: id=" + id);
        }
        repository.deleteById(id);
    }

    // Filtro que aponta para registro inexistente é erro do cliente, não
    // lista vazia: sem isto um id errado passaria por "não tem questão".
    private void validateFilter(QuestionFilterDTO filter) {
        if (filter.entranceExamId() != null
                && !entranceExamRepository.existsById(filter.entranceExamId())) {
            throw new EntityNotFoundException(
                    "EntranceExam não encontrado: id=" + filter.entranceExamId());
        }
        if (filter.knowledgeAreaId() != null
                && !knowledgeAreaRepository.existsById(filter.knowledgeAreaId())) {
            throw new EntityNotFoundException(
                    "KnowledgeArea não encontrada: id=" + filter.knowledgeAreaId());
        }
        if (filter.subjectId() != null && !subjectRepository.existsById(filter.subjectId())) {
            throw new EntityNotFoundException(
                    "Subject não encontrada: id=" + filter.subjectId());
        }
        if (filter.topicId() != null && !topicRepository.existsById(filter.topicId())) {
            throw new EntityNotFoundException("Topic não encontrado: id=" + filter.topicId());
        }
    }

    // A questão é classificada por tópico OU por matéria, nunca pelos dois:
    // o tópico já pertence a uma matéria, então informar ambos permitiria
    // gravar um par contraditório.
    private void validateTopicOrSubject(Long topicId, Long subjectId) {
        if (topicId == null && subjectId == null) {
            throw new IllegalArgumentException(
                    "informe topicId ou subjectId");
        }
        if (topicId != null && subjectId != null) {
            throw new IllegalArgumentException(
                    "informe topicId ou subjectId, não os dois");
        }
    }

    private EntranceExam findEntranceExam(Long entranceExamId) {
        return entranceExamRepository.findById(entranceExamId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "EntranceExam não encontrado: id=" + entranceExamId));
    }

    // topic_id e subject_id são opcionais no banco, então id nulo significa
    // "sem vínculo" e não erro.
    private Topic findTopic(Long topicId) {
        if (topicId == null) {
            return null;
        }
        return topicRepository.findById(topicId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Topic não encontrado: id=" + topicId));
    }

    private Subject findSubject(Long subjectId) {
        if (subjectId == null) {
            return null;
        }
        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Subject não encontrada: id=" + subjectId));
    }
}
