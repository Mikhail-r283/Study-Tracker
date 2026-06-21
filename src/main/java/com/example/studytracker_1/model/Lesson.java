package com.example.studytracker_1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "lessons")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String question;

    @Column(name = "option_1", nullable = false)
    private String option1;

    @Column(name = "option_2", nullable = false)
    private String option2;

    @Column(name = "option_3")
    private String option3;

    @Column(name = "option_4")
    private String option4;

    @Column(name = "correct_answer", nullable = false)
    private Integer correctAnswer;

    @Column(name = "is_answered")
    private Boolean answered = false;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TEXT")
    private String description;        // описание урока

    @Column(name = "difficulty")
    private String difficulty = "EASY"; // EASY, MEDIUM, HARD

    @Column(name = "points")
    private Integer points = 0;        // баллы за урок

    @Column(name = "ai_generated")
    private Boolean aiGenerated = false; // создан ли AI

    public Lesson() {}

    public Lesson(String question, String option1, String option2, String option3, String option4, Integer correctAnswer) {
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.correctAnswer = correctAnswer;
    }

}
