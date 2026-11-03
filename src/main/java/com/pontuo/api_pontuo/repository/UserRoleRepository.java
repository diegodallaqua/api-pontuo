package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.UserRole;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    Optional<UserRole> findByDescription(String description);

    /**
     * Listagem; {@code search} é o padrão de {@link LikePattern#contains}.
     */
    @Query("""
            SELECT r FROM UserRole r
            WHERE (:search IS NULL OR LOWER(r.description) LIKE :search ESCAPE '!')
            """)
    Page<UserRole> search(@Param("search") String search, Pageable pageable);
}
