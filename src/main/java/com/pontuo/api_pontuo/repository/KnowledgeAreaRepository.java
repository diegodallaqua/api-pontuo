package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.KnowledgeArea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface KnowledgeAreaRepository extends JpaRepository<KnowledgeArea, Long> {

    /**
     * Listagem; {@code search} é o padrão de {@link LikePattern#contains}.
     */
    @Query("""
            SELECT k FROM KnowledgeArea k
            WHERE (:search IS NULL OR LOWER(k.description) LIKE :search ESCAPE '!')
            """)
    Page<KnowledgeArea> search(@Param("search") String search, Pageable pageable);
}
