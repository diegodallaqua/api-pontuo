package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.Topic;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    @Override
    @EntityGraph(attributePaths = "subject.knowledgeArea")
    List<Topic> findAll();

    @Override
    @EntityGraph(attributePaths = "subject.knowledgeArea")
    Optional<Topic> findById(Long id);

    @EntityGraph(attributePaths = "subject.knowledgeArea")
    List<Topic> findBySubjectId(Long subjectId);
}
