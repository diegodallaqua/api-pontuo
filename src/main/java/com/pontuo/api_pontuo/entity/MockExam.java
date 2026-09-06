package com.pontuo.api_pontuo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "mock_exam")
public class MockExam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 150)
    private String name;

    @Column(name = "num_questions", columnDefinition = "SMALLINT")
    private short numQuestions;

    @Column(name = "max_time", columnDefinition = "SMALLINT")
    private short maxTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "correct_count", columnDefinition = "SMALLINT")
    private short correctCount;

    @Column(name = "accuracy", precision = 5, scale = 2)
    private BigDecimal accuracy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public MockExam() {
    }

    public MockExam(String name, short numQuestions, short maxTime) {
        this.name = name;
        this.numQuestions = numQuestions;
        this.maxTime = maxTime;
    }

    public MockExam(String name, short numQuestions, short maxTime, User user) {
        this.name = name;
        this.numQuestions = numQuestions;
        this.maxTime = maxTime;
        this.user = user;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getNumQuestions() {
        return numQuestions;
    }

    public void setNumQuestions(short numQuestions) {
        this.numQuestions = numQuestions;
    }

    public short getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(short maxTime) {
        this.maxTime = maxTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public short getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(short correctCount) {
        this.correctCount = correctCount;
    }

    public BigDecimal getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(BigDecimal accuracy) {
        this.accuracy = accuracy;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
