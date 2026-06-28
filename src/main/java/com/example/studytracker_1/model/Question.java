package com.example.studytracker_1.model;

import com.example.studytracker_1.entity.Tag;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter

public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(name = "question_number")
    private int questionNumber;

    @Column(nullable = false, columnDefinition = "TEXT")
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

    @Column(name = "user_answer")
    private Integer userAnswer;                 // <-- ДОБАВЛЯЕМ: ответ пользователя

    @Column(name = "points")
    private Integer points = 10;

    @Column(name = "is_answered")
    private Boolean answered = false;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "hint")
    private String hint;                        // <-- ДОБАВЛЯЕМ: подсказка

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;                 // <-- ДОБАВЛЯЕМ: объяснение правильного ответа

    @Column(name = "difficulty")
    private String difficulty = "EASY";         // <-- ДОБАВЛЯЕМ: сложность вопроса

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "question_tags",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();     // <-- ДОБАВЛЯЕМ: теги


    public Question(String question, String option1, String option2,
                    String option3, String option4, Integer correctAnswer) {
        this();
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.correctAnswer = correctAnswer;
    }

    public void setUserAnswer(Integer userAnswer) {
        this.userAnswer = userAnswer;
        this.answered = userAnswer != null;
        this.isCorrect = userAnswer != null && userAnswer.equals(correctAnswer);
    }

    // Вспомогательные методы
    public void addTag(Tag tag) {
        this.tags.add(tag);
        tag.getQuestions().add(this);
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getQuestions().remove(this);
    }

    public List<String> getOptionsList() {
        return Arrays.asList(option1, option2, option3, option4);
    }

    public String getCorrectAnswerText() {
        return switch (correctAnswer) {
            case 1 -> option1;
            case 2 -> option2;
            case 3 -> option3;
            case 4 -> option4;
            default -> "Неизвестно";
        };
    }

    public String getUserAnswerText() {
        if (userAnswer == null) return "Не отвечен";
        return switch (userAnswer) {
            case 1 -> option1;
            case 2 -> option2;
            case 3 -> option3;
            case 4 -> option4;
            default -> "Неизвестно";
        };
    }

    public String getDifficultyEmoji() {
        return switch (difficulty != null ? difficulty.toUpperCase() : "EASY") {
            case "EASY" -> "🟢";
            case "MEDIUM" -> "🟡";
            case "HARD" -> "🔴";
            default -> "⚪️";
        };
    }

    public boolean isAnsweredCorrectly() {
        return Boolean.TRUE.equals(answered) && Boolean.TRUE.equals(isCorrect);
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", questionNumber=" + questionNumber +
                ", question='" + question + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", answered=" + answered +
                ", isCorrect=" + isCorrect +
                ", tags=" + tags.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question1 = (Question) o;
        return Objects.equals(id, question1.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
