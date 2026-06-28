package com.example.studytracker_1.repository;

import com.example.studytracker_1.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByLessonId(Long lessonId);

    List<Question> findByIsCorrect(Boolean isCorrect);

    List<Question> findByAnswered(Boolean answered);

    long countByLessonIdAndIsCorrect(Long lessonId, Boolean isCorrect);

    long countByLessonIdAndAnswered(Long lessonId, Boolean answered);

    long countByLessonIdIn(List<Long> lessonIds);
    long countByLessonIdInAndAnswered(List<Long> lessonIds, Boolean answered);
    long countByLessonIdInAndIsCorrect(List<Long> lessonIds, Boolean isCorrect);
}