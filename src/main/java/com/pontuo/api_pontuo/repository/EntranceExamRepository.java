package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.EntranceExam;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EntranceExamRepository extends JpaRepository<EntranceExam, Long> {

    @Override
    @EntityGraph(attributePaths = "institution")
    Optional<EntranceExam> findById(Long id);

    /**
     * Listagem: cada filtro nulo se anula, e {@code search} é o padrão de
     * {@link LikePattern#contains}.
     */
    @EntityGraph(attributePaths = "institution")
    @Query("""
            SELECT e FROM EntranceExam e
            WHERE (:institutionId IS NULL OR e.institution.id = :institutionId)
              AND (:year IS NULL OR e.year = :year)
              AND (:search IS NULL
                   OR LOWER(e.name) LIKE :search ESCAPE '!'
                   OR LOWER(e.stage) LIKE :search ESCAPE '!')
            """)
    Page<EntranceExam> search(@Param("institutionId") Long institutionId,
                              @Param("year") Short year,
                              @Param("search") String search,
                              Pageable pageable);
}
