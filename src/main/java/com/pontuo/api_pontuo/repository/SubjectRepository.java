package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.Subject;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    @Override
    @EntityGraph(attributePaths = "knowledgeArea")
    Optional<Subject> findById(Long id);

    /**
     * Listagem: cada filtro nulo se anula, e {@code search} é o padrão de
     * {@link LikePattern#contains}.
     */
    @EntityGraph(attributePaths = "knowledgeArea")
    @Query("""
            SELECT s FROM Subject s
            WHERE (:knowledgeAreaId IS NULL OR s.knowledgeArea.id = :knowledgeAreaId)
              AND (:search IS NULL OR LOWER(s.description) LIKE :search ESCAPE '!')
            """)
    Page<Subject> search(@Param("knowledgeAreaId") Long knowledgeAreaId,
                         @Param("search") String search,
                         Pageable pageable);
}
