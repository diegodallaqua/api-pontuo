package com.pontuo.api_pontuo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "mock_exam_has_question")
public class MockExamHasQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mock_exam_id", nullable = false)
    private MockExam mockExam;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // Alternativa marcada pelo usuário. Fica nula enquanto a questão não
    // foi respondida.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_option_id")
    private AnswerOption answerOption;

    public MockExamHasQuestion() {
    }

    public MockExamHasQuestion(MockExam mockExam, Question question) {
        this.mockExam = mockExam;
        this.question = question;
    }

    public MockExamHasQuestion(MockExam mockExam, Question question,
                               AnswerOption answerOption, LocalDateTime answeredAt) {
        this.mockExam = mockExam;
        this.question = question;
        this.answerOption = answerOption;
        this.answeredAt = answeredAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getAnsweredAt() {
        return answeredAt;
    }

    public void setAnsweredAt(LocalDateTime answeredAt) {
        this.answeredAt = answeredAt;
    }

    public MockExam getMockExam() {
        return mockExam;
    }

    public void setMockExam(MockExam mockExam) {
        this.mockExam = mockExam;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public AnswerOption getAnswerOption() {
        return answerOption;
    }

    public void setAnswerOption(AnswerOption answerOption) {
        this.answerOption = answerOption;
    }
}
