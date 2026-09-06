package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.MockExam;
import com.pontuo.api_pontuo.entity.User;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MockExamRepository extends JpaRepository<MockExam, Long> {

    @Override
    @EntityGraph(attributePaths = "user")
    List<MockExam> findAll();

    @Override
    @EntityGraph(attributePaths = "user")
    Optional<MockExam> findById(Long id);

    @EntityGraph(attributePaths = "user")
    List<MockExam> findByUser(User user);

    @EntityGraph(attributePaths = "user")
    List<MockExam> findByUserId(Long userId);
}
