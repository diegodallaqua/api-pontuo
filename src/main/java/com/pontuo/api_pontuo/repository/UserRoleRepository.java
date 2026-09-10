package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.UserRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    Optional<UserRole> findByDescription(String description);
}