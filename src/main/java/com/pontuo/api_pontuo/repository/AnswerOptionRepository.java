package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.AnswerOption;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerOptionRepository extends JpaRepository<AnswerOption, Long> {

    @Override
    @EntityGraph(attributePaths = {
            "question.entranceExam.institution.address.city.state",
            "question.topic.subject.knowledgeArea",
            "question.subject.knowledgeArea"})
    List<AnswerOption> findAll();

    @Override
    @EntityGraph(attributePaths = {
            "question.entranceExam.institution.address.city.state",
            "question.topic.subject.knowledgeArea",
            "question.subject.knowledgeArea"})
    Optional<AnswerOption> findById(Long id);

    @EntityGraph(attributePaths = {
            "question.entranceExam.institution.address.city.state",
            "question.topic.subject.knowledgeArea",
            "question.subject.knowledgeArea"})
    List<AnswerOption> findByQuestionId(Long questionId);

    /**
     * Alternativas de várias questões numa consulta só. É o que evita o N+1 do
     * sorteio: um simulado de 50 questões viraria 50 idas ao banco com
     * {@link #findByQuestionId(Long)}.
     *
     * <p>O {@code JOIN FETCH} traz a questão junto para que quem agrupa o
     * resultado possa ler {@code getQuestion().getId()} sem disparar consulta.
     */
    @Query("""
            SELECT o FROM AnswerOption o
            JOIN FETCH o.question question
            WHERE question.id IN :questionIds
            ORDER BY question.id ASC, o.letter ASC
            """)
    List<AnswerOption> findByQuestionIdIn(@Param("questionIds") Collection<Long> questionIds);

    boolean existsByQuestionIdAndLetter(Long questionId, String letter);

    boolean existsByQuestionIdAndRightAnswerTrue(Long questionId);
}
