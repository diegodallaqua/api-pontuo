package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    @EntityGraph(attributePaths = "userRole")
    Optional<User> findById(Long id);

    /**
     * Listagem: cada filtro nulo se anula, e {@code search} é o padrão de
     * {@link LikePattern#contains}.
     */
    @EntityGraph(attributePaths = "userRole")
    @Query("""
            SELECT u FROM User u
            WHERE (:userRoleId IS NULL OR u.userRole.id = :userRoleId)
              AND (:search IS NULL
                   OR LOWER(u.username) LIKE :search ESCAPE '!'
                   OR LOWER(u.email) LIKE :search ESCAPE '!')
            """)
    Page<User> search(@Param("userRoleId") Long userRoleId,
                      @Param("search") String search,
                      Pageable pageable);

    @EntityGraph(attributePaths = "userRole")
    Optional<User> findByUsername(String username);

    @EntityGraph(attributePaths = "userRole")
    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
