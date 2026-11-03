package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.City;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    @Override
    @EntityGraph(attributePaths = "state")
    Optional<City> findById(Long id);

    /**
     * Listagem: cada filtro nulo se anula, e {@code search} é o padrão de
     * {@link LikePattern#contains}.
     */
    @EntityGraph(attributePaths = "state")
    @Query("""
            SELECT c FROM City c
            WHERE (:stateId IS NULL OR c.state.id = :stateId)
              AND (:search IS NULL OR LOWER(c.name) LIKE :search ESCAPE '!')
            """)
    Page<City> search(@Param("stateId") Long stateId, @Param("search") String search, Pageable pageable);
}
