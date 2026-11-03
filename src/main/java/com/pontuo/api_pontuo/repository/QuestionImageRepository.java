package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.QuestionImage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionImageRepository extends JpaRepository<QuestionImage, Long> {

    @Override
    @EntityGraph(attributePaths = {
            "question.entranceExam.institution.address.city.state",
            "question.topic.subject.knowledgeArea",
            "question.subject.knowledgeArea"})
    Optional<QuestionImage> findById(Long id);

    /**
     * Listagem: cada filtro nulo se anula, e {@code search} é o padrão de
     * {@link LikePattern#contains}.
     */
    @EntityGraph(attributePaths = {
            "question.entranceExam.institution.address.city.state",
            "question.topic.subject.knowledgeArea",
            "question.subject.knowledgeArea"})
    @Query("""
            SELECT i FROM QuestionImage i
            WHERE (:questionId IS NULL OR i.question.id = :questionId)
              AND (:search IS NULL OR LOWER(i.subtitle) LIKE :search ESCAPE '!')
            """)
    Page<QuestionImage> search(@Param("questionId") Long questionId,
                               @Param("search") String search,
                               Pageable pageable);

    /**
     * Imagens de várias questões numa consulta só, pelo mesmo motivo das
     * alternativas: o sorteio não pode custar uma consulta por questão.
     */
    @Query("""
            SELECT i FROM QuestionImage i
            JOIN FETCH i.question question
            WHERE question.id IN :questionIds
            ORDER BY question.id ASC, i.id ASC
            """)
    List<QuestionImage> findByQuestionIdIn(@Param("questionIds") Collection<Long> questionIds);
}
