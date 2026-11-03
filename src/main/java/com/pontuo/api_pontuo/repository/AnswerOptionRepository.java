package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.AnswerOption;

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
public interface AnswerOptionRepository extends JpaRepository<AnswerOption, Long> {

    @Override
    @EntityGraph(attributePaths = {
            "question.entranceExam.institution.address.city.state",
            "question.topic.subject.knowledgeArea",
            "question.subject.knowledgeArea"})
    Optional<AnswerOption> findById(Long id);

    /**
     * Listagem: cada filtro nulo se anula, e {@code search} é o padrão de
     * {@link LikePattern#contains}.
     */
    @EntityGraph(attributePaths = {
            "question.entranceExam.institution.address.city.state",
            "question.topic.subject.knowledgeArea",
            "question.subject.knowledgeArea"})
    @Query("""
            SELECT o FROM AnswerOption o
            WHERE (:questionId IS NULL OR o.question.id = :questionId)
              AND (:search IS NULL OR LOWER(o.text) LIKE :search ESCAPE '!')
            """)
    Page<AnswerOption> search(@Param("questionId") Long questionId,
                              @Param("search") String search,
                              Pageable pageable);

    /**
     * Alternativas de várias questões numa consulta só. É o que evita o N+1 do
     * sorteio: um simulado de 50 questões viraria 50 idas ao banco.
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
