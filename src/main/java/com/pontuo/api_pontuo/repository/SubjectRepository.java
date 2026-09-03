package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.Subject;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    @Override
    @EntityGraph(attributePaths = "knowledgeArea")
    List<Subject> findAll();

    @Override
    @EntityGraph(attributePaths = "knowledgeArea")
    Optional<Subject> findById(Long id);

    @EntityGraph(attributePaths = "knowledgeArea")
    List<Subject> findByKnowledgeAreaId(Long knowledgeAreaId);
}
