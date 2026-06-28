package com.example.studytracker_1.service;

import com.example.studytracker_1.dto.ProgressRequest;
import com.example.studytracker_1.entity.UserLessonProgress;
import com.example.studytracker_1.model.Lesson;
import com.example.studytracker_1.model.User;
import com.example.studytracker_1.repository.UserLessonProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class UserLessonProgressService {

    private final UserLessonProgressRepository repository;

    // Убираем userLessonProgress из конструктора
    public UserLessonProgressService(UserLessonProgressRepository repository) {
        this.repository = repository;
    }

    /**
     * Сохранить или обновить прогресс пользователя по уроку
     */
    public UserLessonProgress save(User user, Lesson lesson, ProgressRequest request) {
        Optional<UserLessonProgress> existing = repository.findByUserIdAndLessonId(user.getId(), lesson.getId());

        UserLessonProgress progress;
        if (existing.isPresent()) {
            progress = existing.get();
        } else {
            progress = new UserLessonProgress();  // создаём новый объект здесь, а не через DI
            progress.setUser(user);
            progress.setLesson(lesson);
        }

        progress.setCorrectAnswers(request.getCorrectAnswers());
        progress.setTotalQuestions(request.getTotalQuestions());
        progress.setPercentage(request.getPercentage());
        progress.setAnswered(true);
        progress.setCompletedAt(LocalDateTime.now());

        return repository.save(progress);
    }

    /**
     * Получить прогресс пользователя по конкретному уроку
     */
    public Optional<UserLessonProgress> getProgressByUserAndLesson(Long userId, Long lessonId) {
        return repository.findByUserIdAndLessonId(userId, lessonId);
    }

    /**
     * Проверить, проходил ли пользователь урок
     */
    public boolean isLessonCompletedByUser(Long userId, Long lessonId) {
        return repository.existsByUserIdAndLessonId(userId, lessonId);
    }

    /**
     * Получить все прогрессы пользователя
     */
    public List<UserLessonProgress> getAllProgressByUser(Long userId) {
        return repository.findAllByUserId(userId);
    }

    /**
     * Получить количество пройденных уроков пользователем
     */
    public long getCompletedLessonsCount(Long userId) {
        return repository.countByUserIdAndAnsweredTrue(userId);
    }

    /**
     * Получить средний процент прохождения уроков пользователем
     */
    public double getAveragePercentage(Long userId) {
        List<UserLessonProgress> progresses = repository.findAllByUserId(userId);
        if (progresses.isEmpty()) return 0.0;

        return progresses.stream()
                .filter(UserLessonProgress::isAnswered)
                .mapToDouble(UserLessonProgress::getPercentage)  // mapToDouble для double
                .average()
                .orElse(0.0);
    }

    /**
     * Удалить прогресс пользователя по уроку
     */
    public void deleteProgress(Long userId, Long lessonId) {
        repository.findByUserIdAndLessonId(userId, lessonId)
                .ifPresent(repository::delete);
    }
}