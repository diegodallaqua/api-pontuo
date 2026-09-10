package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    @EntityGraph(attributePaths = "userRole")
    List<User> findAll();

    @Override
    @EntityGraph(attributePaths = "userRole")
    Optional<User> findById(Long id);

    @EntityGraph(attributePaths = "userRole")
    List<User> findByUserRoleId(Long userRoleId);

    @EntityGraph(attributePaths = "userRole")
    Optional<User> findByUsername(String username);

    @EntityGraph(attributePaths = "userRole")
    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
