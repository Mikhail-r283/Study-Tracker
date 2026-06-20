package com.example.studytracker_1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "statistics")
public class Statistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_lessons", nullable = false)
    private int totalLessons = 0;

    @Column(name = "answered_count", nullable = false)
    private int answeredCount = 0;

    @Column(name = "correct_count", nullable = false)
    private int correctCount = 0;
    @Column(name = "incorrect_count", nullable = false)
    private int incorrectCount = 0;

    @Column(name = "score", nullable = false)
    private int score = 0;

    @Column(name = "date", nullable = false)
    private LocalDate date = LocalDate.now();

    public Statistics() {
    }
}

