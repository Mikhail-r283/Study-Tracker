package com.example.studytracker_1.model;

import com.example.studytracker_1.entity.Tag;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

@Entity
@Table(name = "lessons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // название урока

    @Column(columnDefinition = "TEXT")
    private String description; // описание урока

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("questionNumber ASC")
    private List<Question> questions = new ArrayList<>();

    @Column(name = "is_answered")
    private Boolean answered = false; // отвечал ли пользователь

    // ===== ПОЛЯ ДЛЯ ПРОХОЖДЕНИЯ =====
    @Column(name = "difficulty")
    private Difficulty difficulty = Difficulty.EASY; // EASY, MEDIUM, HARD

    @Column(name = "points")
    private Integer points = 0; // баллы за урок

    @Column(name = "total_points")
    private Integer totalPoints = 0; // всего баллов (если нужно)

    @Column(name = "ai_generated")
    private Boolean aiGenerated = false; // создан ли AI

    // ===== СВЯЗИ =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // кто создал/проходит

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "lesson_tags",
            joinColumns = @JoinColumn(name = "lesson_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    public Lesson(List<Question> questions) {
        this.questions = questions != null ? questions : new ArrayList<>();
        for (Question q : this.questions) {
            q.setLesson(this); // каждый вопрос знает свой урок
        }
    }

    // Новый конструктор с названием
    public Lesson(String title, String description, Difficulty difficulty, List<Question> list, Set<Tag> tags) {
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.questions = list;
        this.tags = tags;
    }
    // Новый конструктор с названием
    public Lesson(String title, String description) {
        this.title = title;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", questions=" + questions +
                ", answered=" + answered +
                ", difficulty='" + difficulty + '\'' +
                ", points=" + points +
                ", totalPoints=" + totalPoints +
                ", aiGenerated=" + aiGenerated +
                ", user=" + user +
                ", tags=" + tags +
                '}';
    }
}