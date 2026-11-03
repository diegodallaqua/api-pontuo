package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {

    /**
     * Listagem; {@code search} é o padrão de {@link LikePattern#contains}.
     */
    @Query("""
            SELECT s FROM State s
            WHERE (:search IS NULL OR LOWER(s.name) LIKE :search ESCAPE '!')
            """)
    Page<State> search(@Param("search") String search, Pageable pageable);
}
