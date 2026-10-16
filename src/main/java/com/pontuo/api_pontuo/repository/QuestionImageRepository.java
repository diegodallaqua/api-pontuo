package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.QuestionImage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionImageRepository extends JpaRepository<QuestionImage, Long> {

    @Override
    @EntityGraph(attributePaths = {
            "question.entranceExam.institution.address.city.state",
            "question.topic.subject.knowledgeArea",
            "question.subject.knowledgeArea"})
    List<QuestionImage> findAll();

    @Override
    @EntityGraph(attributePaths = {
            "question.entranceExam.institution.address.city.state",
            "question.topic.subject.knowledgeArea",
            "question.subject.knowledgeArea"})
    Optional<QuestionImage> findById(Long id);

    @EntityGraph(attributePaths = {
            "question.entranceExam.institution.address.city.state",
            "question.topic.subject.knowledgeArea",
            "question.subject.knowledgeArea"})
    List<QuestionImage> findByQuestionId(Long questionId);

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
