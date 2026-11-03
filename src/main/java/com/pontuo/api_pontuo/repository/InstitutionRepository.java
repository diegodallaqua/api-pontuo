package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.Institution;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long> {

    @Override
    @EntityGraph(attributePaths = "address")
    Optional<Institution> findById(Long id);

    /**
     * Listagem: cada filtro nulo se anula, e {@code search} é o padrão de
     * {@link LikePattern#contains}.
     */
    @EntityGraph(attributePaths = "address")
    @Query("""
            SELECT i FROM Institution i
            WHERE (:acronym IS NULL OR i.acronym = :acronym)
              AND (:search IS NULL
                   OR LOWER(i.name) LIKE :search ESCAPE '!'
                   OR LOWER(i.acronym) LIKE :search ESCAPE '!')
            """)
    Page<Institution> search(@Param("acronym") String acronym,
                             @Param("search") String search,
                             Pageable pageable);
}
