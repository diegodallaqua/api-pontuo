package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.EntranceExam;
import com.pontuo.api_pontuo.entity.Question;
import com.pontuo.api_pontuo.entity.Subject;
import com.pontuo.api_pontuo.entity.Topic;
import com.pontuo.api_pontuo.repository.EntranceExamRepository;
import com.pontuo.api_pontuo.repository.QuestionRepository;
import com.pontuo.api_pontuo.repository.SubjectRepository;
import com.pontuo.api_pontuo.repository.TopicRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    private final QuestionRepository repository;
    private final EntranceExamRepository entranceExamRepository;
    private final TopicRepository topicRepository;
    private final SubjectRepository subjectRepository;

    public QuestionService(QuestionRepository repository,
                           EntranceExamRepository entranceExamRepository,
                           TopicRepository topicRepository,
                           SubjectRepository subjectRepository) {
        this.repository = repository;
        this.entranceExamRepository = entranceExamRepository;
        this.topicRepository = topicRepository;
        this.subjectRepository = subjectRepository;
    }

    public List<Question> findAll() {
        return repository.findAll();
    }

    public List<Question> findByEntranceExamId(Long entranceExamId) {
        if (!entranceExamRepository.existsById(entranceExamId)) {
            throw new EntityNotFoundException(
                    "EntranceExam não encontrado: id=" + entranceExamId);
        }
        return repository.findByEntranceExamId(entranceExamId);
    }

    public List<Question> findByTopicId(Long topicId) {
        if (!topicRepository.existsById(topicId)) {
            throw new EntityNotFoundException("Topic não encontrado: id=" + topicId);
        }
        return repository.findByTopicId(topicId);
    }

    public List<Question> findBySubjectId(Long subjectId) {
        if (!subjectRepository.existsById(subjectId)) {
            throw new EntityNotFoundException("Subject não encontrada: id=" + subjectId);
        }
        return repository.findBySubjectId(subjectId);
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
