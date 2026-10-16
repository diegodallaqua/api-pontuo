package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.MockExamHasQuestion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MockExamHasQuestionRepository extends JpaRepository<MockExamHasQuestion, Long> {

    /**
     * Vínculos com o simulado e a classificação da questão. Os joins são LEFT
     * porque a questão tem tópico OU matéria.
     */
    String FROM_LINKS = """
            FROM MockExamHasQuestion l
            JOIN l.mockExam m
            JOIN l.question q
            LEFT JOIN q.topic t
            LEFT JOIN t.subject ts
            LEFT JOIN ts.knowledgeArea tka
            LEFT JOIN q.subject qs
            LEFT JOIN qs.knowledgeArea qka
            """;

    /**
     * Regra única de matéria de toda a análise de desempenho: a do tópico da
     * questão ou, sem tópico, a da própria questão. Vale a classificação atual,
     * então reclassificar uma questão reflete em todos os relatórios igualmente.
     */
    String SUBJECT_ID = "coalesce(ts.id, qs.id)";
    String SUBJECT_DESCRIPTION = "coalesce(ts.description, qs.description)";
    String KNOWLEDGE_AREA_ID = "coalesce(tka.id, qka.id)";
    String KNOWLEDGE_AREA_DESCRIPTION = "coalesce(tka.description, qka.description)";

    /** Só simulados finalizados do usuário, cujas respostas estão travadas. {@code to} é exclusivo. */
    String FINISHED_BY_USER = """
            WHERE m.user.id = :userId
              AND m.finishedAt IS NOT NULL
              AND (:from IS NULL OR m.finishedAt >= :from)
              AND (:to IS NULL OR m.finishedAt < :to)
            """;

    String ENTRANCE_EXAM_FILTER = """
              AND (:entranceExamId IS NULL OR q.entranceExam.id = :entranceExamId)
            """;

    /** Questões, respondidas e corretas, fechando o construtor da projeção. */
    String COUNTS = """
            count(l),
            sum(CASE WHEN l.answeredAt IS NOT NULL THEN 1 ELSE 0 END),
            sum(CASE WHEN l.isCorrect = true THEN 1 ELSE 0 END))
            """;

    @Override
    @EntityGraph(attributePaths = {
            "mockExam.user.userRole",
            "question.entranceExam.institution.address.city.state",
            "question.topic.subject.knowledgeArea",
            "question.subject.knowledgeArea",
            "answerOption.question"})
    List<MockExamHasQuestion> findAll();

    @Override
    @EntityGraph(attributePaths = {
            "mockExam.user.userRole",
            "question.entranceExam.institution.address.city.state",
            "question.topic.subject.knowledgeArea",
            "question.subject.knowledgeArea",
            "answerOption.question"})
    Optional<MockExamHasQuestion> findById(Long id);

    @EntityGraph(attributePaths = {
            "mockExam.user.userRole",
            "question.entranceExam.institution.address.city.state",
            "question.topic.subject.knowledgeArea",
            "question.subject.knowledgeArea",
            "answerOption.question"})
    List<MockExamHasQuestion> findByMockExamId(Long mockExamId);

    @EntityGraph(attributePaths = {
            "mockExam.user.userRole",
            "question.entranceExam.institution.address.city.state",
            "question.topic.subject.knowledgeArea",
            "question.subject.knowledgeArea",
            "answerOption.question"})
    List<MockExamHasQuestion> findByQuestionId(Long questionId);

    @EntityGraph(attributePaths = {
            "mockExam.user.userRole",
            "question.entranceExam.institution.address.city.state",
            "question.topic.subject.knowledgeArea",
            "question.subject.knowledgeArea",
            "answerOption.question"})
    List<MockExamHasQuestion> findByMockExamUserId(Long userId);

    @EntityGraph(attributePaths = {
            "mockExam.user.userRole",
            "question.entranceExam.institution.address.city.state",
            "question.topic.subject.knowledgeArea",
            "question.subject.knowledgeArea",
            "answerOption.question"})
    List<MockExamHasQuestion> findByQuestionIdAndMockExamUserId(Long questionId, Long userId);

    boolean existsByMockExamIdAndQuestionId(Long mockExamId, Long questionId);

    long countByMockExamId(Long mockExamId);

    @Query("SELECT new com.pontuo.api_pontuo.repository.PerformanceRow("
            + KNOWLEDGE_AREA_ID + ", " + KNOWLEDGE_AREA_DESCRIPTION + ", "
            + COUNTS + FROM_LINKS + FINISHED_BY_USER + ENTRANCE_EXAM_FILTER
            + " AND " + KNOWLEDGE_AREA_ID + " IS NOT NULL"
            + " GROUP BY " + KNOWLEDGE_AREA_ID + ", " + KNOWLEDGE_AREA_DESCRIPTION
            + " ORDER BY " + KNOWLEDGE_AREA_DESCRIPTION)
    List<PerformanceRow> performanceByKnowledgeArea(@Param("userId") Long userId,
                                                    @Param("from") LocalDateTime from,
                                                    @Param("to") LocalDateTime to,
                                                    @Param("entranceExamId") Long entranceExamId);

    @Query("SELECT new com.pontuo.api_pontuo.repository.PerformanceRow("
            + SUBJECT_ID + ", " + SUBJECT_DESCRIPTION + ", "
            + COUNTS + FROM_LINKS + FINISHED_BY_USER + ENTRANCE_EXAM_FILTER
            + " AND " + SUBJECT_ID + " IS NOT NULL"
            + " AND (:knowledgeAreaId IS NULL OR " + KNOWLEDGE_AREA_ID + " = :knowledgeAreaId)"
            + " GROUP BY " + SUBJECT_ID + ", " + SUBJECT_DESCRIPTION
            + " ORDER BY " + SUBJECT_DESCRIPTION)
    List<PerformanceRow> performanceBySubject(@Param("userId") Long userId,
                                              @Param("from") LocalDateTime from,
                                              @Param("to") LocalDateTime to,
                                              @Param("entranceExamId") Long entranceExamId,
                                              @Param("knowledgeAreaId") Long knowledgeAreaId);

    /** Só questões classificadas por tópico entram aqui. */
    @Query("SELECT new com.pontuo.api_pontuo.repository.PerformanceRow(t.id, t.description, "
            + COUNTS + FROM_LINKS + FINISHED_BY_USER + ENTRANCE_EXAM_FILTER
            + " AND t.id IS NOT NULL"
            + " AND (:subjectId IS NULL OR ts.id = :subjectId)"
            + " GROUP BY t.id, t.description"
            + " ORDER BY t.description")
    List<PerformanceRow> performanceByTopic(@Param("userId") Long userId,
                                            @Param("from") LocalDateTime from,
                                            @Param("to") LocalDateTime to,
                                            @Param("entranceExamId") Long entranceExamId,
                                            @Param("subjectId") Long subjectId);

    @Query("SELECT new com.pontuo.api_pontuo.repository.PerformanceRow("
            + "cast(q.difficulty as Long), cast(null as String), "
            + COUNTS + FROM_LINKS + FINISHED_BY_USER + ENTRANCE_EXAM_FILTER
            + " GROUP BY q.difficulty"
            + " ORDER BY q.difficulty")
    List<PerformanceRow> performanceByDifficulty(@Param("userId") Long userId,
                                                 @Param("from") LocalDateTime from,
                                                 @Param("to") LocalDateTime to,
                                                 @Param("entranceExamId") Long entranceExamId);

    /** Boletim por matéria de um simulado. */
    @Query("SELECT new com.pontuo.api_pontuo.repository.PerformanceRow("
            + SUBJECT_ID + ", " + SUBJECT_DESCRIPTION + ", "
            + COUNTS + FROM_LINKS
            + " WHERE m.id = :mockExamId"
            + " AND " + SUBJECT_ID + " IS NOT NULL"
            + " GROUP BY " + SUBJECT_ID + ", " + SUBJECT_DESCRIPTION
            + " ORDER BY " + SUBJECT_DESCRIPTION)
    List<PerformanceRow> performanceBySubjectOfMockExam(@Param("mockExamId") Long mockExamId);

    /** Resultado de cada simulado finalizado do usuário, restrito às questões da matéria. */
    @Query("SELECT new com.pontuo.api_pontuo.repository.EvolutionRow(m.id, m.name, m.finishedAt, "
            + COUNTS + FROM_LINKS + FINISHED_BY_USER
            + " AND " + SUBJECT_ID + " = :subjectId"
            + " GROUP BY m.id, m.name, m.finishedAt"
            + " ORDER BY m.finishedAt, m.id")
    List<EvolutionRow> evolutionBySubject(@Param("userId") Long userId,
                                          @Param("subjectId") Long subjectId,
                                          @Param("from") LocalDateTime from,
                                          @Param("to") LocalDateTime to);
}
