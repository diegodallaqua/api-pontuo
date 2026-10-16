package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.Question;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * Um único WHERE para as três consultas de busca (página, contagem e ids
     * do sorteio), para não existir a chance de uma filtrar diferente da outra.
     * Cada filtro nulo se anula, então eles se combinam em AND.
     *
     * <p>Os joins são LEFT de propósito: tópico e matéria são opcionais, e um
     * INNER join aqui esconderia questões classificadas pelo outro caminho.
     */
    String SEARCH_WHERE = """
            WHERE (:entranceExamId IS NULL OR entranceExam.id = :entranceExamId)
              AND (:topicId IS NULL OR topic.id = :topicId)
              AND (:subjectId IS NULL
                   OR subject.id = :subjectId
                   OR topicSubject.id = :subjectId)
              AND (:knowledgeAreaId IS NULL
                   OR knowledgeArea.id = :knowledgeAreaId
                   OR topicKnowledgeArea.id = :knowledgeAreaId)
              AND (:difficulty IS NULL OR q.difficulty = :difficulty)
            """;

    String SEARCH_JOINS = """
            LEFT JOIN q.entranceExam entranceExam
            LEFT JOIN q.topic topic
            LEFT JOIN topic.subject topicSubject
            LEFT JOIN topicSubject.knowledgeArea topicKnowledgeArea
            LEFT JOIN q.subject subject
            LEFT JOIN subject.knowledgeArea knowledgeArea
            """;

    /** Mesmos joins da busca, com FETCH para a resposta não fazer N+1. */
    String SEARCH_FETCH_JOINS = """
            LEFT JOIN FETCH q.entranceExam entranceExam
            LEFT JOIN FETCH entranceExam.institution institution
            LEFT JOIN FETCH institution.address address
            LEFT JOIN FETCH address.city city
            LEFT JOIN FETCH city.state state
            LEFT JOIN FETCH q.topic topic
            LEFT JOIN FETCH topic.subject topicSubject
            LEFT JOIN FETCH topicSubject.knowledgeArea topicKnowledgeArea
            LEFT JOIN FETCH q.subject subject
            LEFT JOIN FETCH subject.knowledgeArea knowledgeArea
            """;

    @Override
    @EntityGraph(attributePaths = {
            "entranceExam.institution.address.city.state",
            "topic.subject.knowledgeArea",
            "subject.knowledgeArea"})
    List<Question> findAll();

    @Override
    @EntityGraph(attributePaths = {
            "entranceExam.institution.address.city.state",
            "topic.subject.knowledgeArea",
            "subject.knowledgeArea"})
    Optional<Question> findById(Long id);

    @Query(value = "SELECT q FROM Question q " + SEARCH_FETCH_JOINS + SEARCH_WHERE,
            countQuery = "SELECT count(q) FROM Question q " + SEARCH_JOINS + SEARCH_WHERE)
    Page<Question> search(@Param("entranceExamId") Long entranceExamId,
                          @Param("knowledgeAreaId") Long knowledgeAreaId,
                          @Param("subjectId") Long subjectId,
                          @Param("topicId") Long topicId,
                          @Param("difficulty") Short difficulty,
                          Pageable pageable);

    /**
     * Só os ids que passam nos filtros. O sorteio embaralha esta lista em vez
     * de pedir ORDER BY RAND() ao banco, que varia de dialeto e não permite
     * reaproveitar o mesmo WHERE.
     */
    @Query("SELECT q.id FROM Question q " + SEARCH_JOINS + SEARCH_WHERE)
    List<Long> searchIds(@Param("entranceExamId") Long entranceExamId,
                         @Param("knowledgeAreaId") Long knowledgeAreaId,
                         @Param("subjectId") Long subjectId,
                         @Param("topicId") Long topicId,
                         @Param("difficulty") Short difficulty);

    @EntityGraph(attributePaths = {
            "entranceExam.institution.address.city.state",
            "topic.subject.knowledgeArea",
            "subject.knowledgeArea"})
    List<Question> findByIdIn(Collection<Long> ids);
}
