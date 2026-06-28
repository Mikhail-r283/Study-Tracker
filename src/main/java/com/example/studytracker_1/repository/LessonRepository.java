package com.example.studytracker_1.repository;

import com.example.studytracker_1.dto.LessonSummaryDto;
import com.example.studytracker_1.model.Lesson;
import com.example.studytracker_1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findByAnsweredFalse();

    long countByAnsweredTrue();
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM lessons WHERE id = :lessonId AND (user_id = :userId OR user_id IS NULL)", nativeQuery = true)
    void deleteByIdOrUserIdNull(@Param("lessonId") Long lessonId, @Param("userId") Long userId);

    List<Lesson> findByUser(User user);
    List<Lesson> findByUserIsNull();
    List<Lesson> findByUserId(Long userId);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM lesson_tags WHERE lesson_id = :lessonId", nativeQuery = true)
    void deleteLessonTagsByLessonId(@Param("lessonId") Long lessonId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM questions WHERE lesson_id = :lessonId", nativeQuery = true)
    void deleteQuestionsByLessonId(@Param("lessonId") Long lessonId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM userlessonprogress WHERE lesson_id = :lessonId", nativeQuery = true)
    void deleteUserProgressByLessonId(@Param("lessonId") Long lessonId);
}