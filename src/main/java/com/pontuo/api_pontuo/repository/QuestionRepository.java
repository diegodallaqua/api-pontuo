package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.Question;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Override
    @EntityGraph(attributePaths = {
            "entranceExam.institution.address.city.state",
            "topic.subject.knowledgeArea",
            "subject.knowledgeArea"})
    List<Question> findAll();

    @Override
    @EntityGraph(attributePaths = {
            "entranceExam.institution.address.city.state",
            "topic.subject.knowledgeArea",
            "subject.knowledgeArea"})
    Optional<Question> findById(Long id);

    @EntityGraph(attributePaths = {
            "entranceExam.institution.address.city.state",
            "topic.subject.knowledgeArea",
            "subject.knowledgeArea"})
    List<Question> findByEntranceExamId(Long entranceExamId);

    @EntityGraph(attributePaths = {
            "entranceExam.institution.address.city.state",
            "topic.subject.knowledgeArea",
            "subject.knowledgeArea"})
    List<Question> findByTopicId(Long topicId);

    @EntityGraph(attributePaths = {
            "entranceExam.institution.address.city.state",
            "topic.subject.knowledgeArea",
            "subject.knowledgeArea"})
    List<Question> findBySubjectId(Long subjectId);
}
