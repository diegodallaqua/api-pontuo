package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.Topic;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    @Override
    @EntityGraph(attributePaths = "subject.knowledgeArea")
    Optional<Topic> findById(Long id);

    /**
     * Listagem: cada filtro nulo se anula, e {@code search} é o padrão de
     * {@link LikePattern#contains}.
     */
    @EntityGraph(attributePaths = "subject.knowledgeArea")
    @Query("""
            SELECT t FROM Topic t
            WHERE (:subjectId IS NULL OR t.subject.id = :subjectId)
              AND (:search IS NULL OR LOWER(t.description) LIKE :search ESCAPE '!')
            """)
    Page<Topic> search(@Param("subjectId") Long subjectId, @Param("search") String search, Pageable pageable);
}
