package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.MockExamHasQuestion;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MockExamHasQuestionRepository extends JpaRepository<MockExamHasQuestion, Long> {

    @Override
    @EntityGraph(attributePaths = {
            "mockExam.user.userRole",
            "question.entranceExam.institution.address.city.state",
            "question.topic.subject.knowledgeArea",
            "question.subject.knowledgeArea",
            "answerOption.question"})
    List<MockExamHasQuestion> findAll();

    @Override
    @EntityGraph(attributePaths = {
            "mockExam.user.userRole",
            "question.entranceExam.institution.address.city.state",
            "question.topic.subject.knowledgeArea",
            "question.subject.knowledgeArea",
            "answerOption.question"})
    Optional<MockExamHasQuestion> findById(Long id);

    @EntityGraph(attributePaths = {
            "mockExam.user.userRole",
            "question.entranceExam.institution.address.city.state",
            "question.topic.subject.knowledgeArea",
            "question.subject.knowledgeArea",
            "answerOption.question"})
    List<MockExamHasQuestion> findByMockExamId(Long mockExamId);

    @EntityGraph(attributePaths = {
            "mockExam.user.userRole",
            "question.entranceExam.institution.address.city.state",
            "question.topic.subject.knowledgeArea",
            "question.subject.knowledgeArea",
            "answerOption.question"})
    List<MockExamHasQuestion> findByQuestionId(Long questionId);

    boolean existsByMockExamIdAndQuestionId(Long mockExamId, Long questionId);
}
