package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.AnswerOption;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerOptionRepository extends JpaRepository<AnswerOption, Long> {

    @Override
    @EntityGraph(attributePaths = {
            "question.entranceExam.institution.address.city.state",
            "question.topic.subject.knowledgeArea",
            "question.subject.knowledgeArea"})
    List<AnswerOption> findAll();

    @Override
    @EntityGraph(attributePaths = {
            "question.entranceExam.institution.address.city.state",
            "question.topic.subject.knowledgeArea",
            "question.subject.knowledgeArea"})
    Optional<AnswerOption> findById(Long id);

    @EntityGraph(attributePaths = {
            "question.entranceExam.institution.address.city.state",
            "question.topic.subject.knowledgeArea",
            "question.subject.knowledgeArea"})
    List<AnswerOption> findByQuestionId(Long questionId);

    boolean existsByQuestionIdAndLetter(Long questionId, String letter);

    boolean existsByQuestionIdAndRightAnswerTrue(Long questionId);
}
