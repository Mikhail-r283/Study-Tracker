package com.example.studytracker_1.repository;

import com.example.studytracker_1.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findByAnsweredFalse();

    long countByAnsweredTrue();

    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.isCorrect = true")
    long countCorrect();

    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.isCorrect = false")
    long countIncorrect();

    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.isCorrect IS NOT NULL")
    long countAnswered();
}