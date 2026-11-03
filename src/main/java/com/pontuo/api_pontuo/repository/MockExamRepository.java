package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.MockExam;

import java.time.LocalDateTime;
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
public interface MockExamRepository extends JpaRepository<MockExam, Long> {

    @Override
    @EntityGraph(attributePaths = "user")
    Optional<MockExam> findById(Long id);

    /**
     * Listagem: cada filtro nulo se anula, e {@code search} é o padrão de
     * {@link LikePattern#contains}.
     */
    @EntityGraph(attributePaths = "user")
    @Query("""
            SELECT m FROM MockExam m
            WHERE (:userId IS NULL OR m.user.id = :userId)
              AND (:search IS NULL OR LOWER(m.name) LIKE :search ESCAPE '!')
            """)
    Page<MockExam> search(@Param("userId") Long userId, @Param("search") String search, Pageable pageable);

    /**
     * Simulados finalizados do usuário, do mais antigo para o mais novo. É a
     * base do resumo e da evolução geral do desempenho. {@code to} é exclusivo.
     */
    @Query("""
            SELECT m FROM MockExam m
            WHERE m.user.id = :userId
              AND m.finishedAt IS NOT NULL
              AND (:from IS NULL OR m.finishedAt >= :from)
              AND (:to IS NULL OR m.finishedAt < :to)
            ORDER BY m.finishedAt, m.id
            """)
    List<MockExam> findFinished(@Param("userId") Long userId,
                                @Param("from") LocalDateTime from,
                                @Param("to") LocalDateTime to);
}
