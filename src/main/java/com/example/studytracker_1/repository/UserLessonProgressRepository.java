package com.example.studytracker_1.repository;

import com.example.studytracker_1.entity.UserLessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UserLessonProgressRepository extends JpaRepository<UserLessonProgress, Long> {

    Optional<UserLessonProgress> findByUserIdAndLessonId(Long userId, Long lessonId);

    boolean existsByUserIdAndLessonId(Long userId, Long lessonId);

    List<UserLessonProgress> findAllByUserId(Long userId);

    long countByUserIdAndAnsweredTrue(Long userId);
}