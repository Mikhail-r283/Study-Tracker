package com.example.studytracker_1.entity;

import com.example.studytracker_1.model.Lesson;
import com.example.studytracker_1.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@Entity
@Table(name = "userlessonprogress")
public class UserLessonProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @Column(name = "current_question")
    private int currentQuestion = 0;

    @Column(name = "completed")
    private boolean completed = false;

    @Column(name = "correct_answers")
    private int correctAnswers = 0;

    @Column(name = "total_questions")
    private int totalQuestions = 0;

    @Column(name = "score")
    private int score = 0;

    @Column(name = "percentage")
    private double percentage = 0.0;

    @Column(name = "is_answered")
    private boolean answered = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
// Ответы пользователя (questionNumber -> selectedAnswer)
@ElementCollection
@CollectionTable(name = "user_answers",
        joinColumns = @JoinColumn(name = "progress_id"))
@MapKeyColumn(name = "question_number")
@Column(name = "selected_answer")
private Map<Integer, Integer> answers = new HashMap<>();

    // ===== МЕТОДЫ =====
    public boolean isAnswered() {
        return answered || completed || !answers.isEmpty();
    }

    public double getPercentage() {
        if (totalQuestions == 0) return 0.0;
        return (double) correctAnswers / totalQuestions * 100;
    }

    public UserLessonProgress() {}
}