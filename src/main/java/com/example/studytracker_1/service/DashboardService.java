package com.example.studytracker_1.service;

import com.example.studytracker_1.model.Lesson;
import com.example.studytracker_1.model.Statistics;
import com.example.studytracker_1.model.User;
import com.example.studytracker_1.repository.LessonRepository;
import com.example.studytracker_1.repository.StatisticsRepository;
import com.example.studytracker_1.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DashboardService {

    private final LessonRepository lessonRepository;
    private final StatisticsRepository statisticsRepository;
    private final UserRepository userRepository;

    public DashboardService(LessonRepository lessonRepository, StatisticsRepository statisticsRepository, UserRepository userRepository) {
        this.lessonRepository = lessonRepository;
        this.statisticsRepository = statisticsRepository;
        this.userRepository = userRepository;
    }

    public Map<String, Object> getStatsForUser(String username) {
        Map<String, Object> stats = new HashMap<>();
        long startTime = System.currentTimeMillis();

        try {
            // Получаем пользователя
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            // Получаем все уроки пользователя
            List<Lesson> userLessons = lessonRepository.findByUserId(user.getId());

            // Считаем статистику
            long totalLessons = userLessons.size();

            // Исправлено: используем правильные методы Boolean
            long answeredCount = userLessons.stream()
                    .filter(lesson -> lesson.getAnswered() == Boolean.TRUE)
                    .count();

            long correctCount = userLessons.stream()
                    .filter(lesson -> {
                        Boolean isCorrect = lesson.getIsCorrect();
                        return isCorrect != null && isCorrect;
                    })
                    .count();

            long incorrectCount = answeredCount - correctCount;
            if (incorrectCount < 0) incorrectCount = 0;

            double percent = answeredCount > 0 ? (correctCount * 100.0 / answeredCount) : 0;

            stats.put("totalLessons", totalLessons);
            stats.put("answeredCount", answeredCount);
            stats.put("correctCount", correctCount);
            stats.put("incorrectCount", incorrectCount);
            stats.put("correctPercent", Math.round(percent * 100.0) / 100.0);
            stats.put("score", (int) Math.round(percent));

            System.out.println("✅ Статистика для " + username +
                    ": всего=" + totalLessons +
                    ", отвечено=" + answeredCount +
                    ", правильно=" + correctCount +
                    ", время=" + (System.currentTimeMillis() - startTime) + "ms");

        } catch (Exception e) {
            System.out.println("❌ Ошибка в getStatsForUser: " + e.getMessage());
            e.printStackTrace();

            // Запасные данные
            stats.put("totalLessons", 0L);
            stats.put("answeredCount", 0L);
            stats.put("correctCount", 0L);
            stats.put("incorrectCount", 0L);
            stats.put("correctPercent", 0.0);
            stats.put("score", 0);
        }

        return stats;
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        try {
            List<Lesson> allLessons = lessonRepository.findAll();
            long totalLessons = allLessons.size();

            // Исправлено: используем Boolean
            long answeredCount = allLessons.stream()
                    .filter(lesson -> lesson.getAnswered() == Boolean.TRUE)
                    .count();

            long correctCount = allLessons.stream()
                    .filter(lesson -> {
                        Boolean isCorrect = lesson.getIsCorrect();
                        return isCorrect != null && isCorrect;
                    })
                    .count();

            long incorrectCount = answeredCount - correctCount;
            if (incorrectCount < 0) incorrectCount = 0;

            double percent = answeredCount > 0 ? (correctCount * 100.0 / answeredCount) : 0;

            stats.put("totalLessons", totalLessons);
            stats.put("answeredCount", answeredCount);
            stats.put("correctCount", correctCount);
            stats.put("incorrectCount", incorrectCount);
            stats.put("correctPercent", Math.round(percent * 100.0) / 100.0);
            stats.put("score", (int) Math.round(percent));

            System.out.println("✅ Общая статистика: всего=" + totalLessons);

        } catch (Exception e) {
            System.out.println("❌ Ошибка в getStats: " + e.getMessage());
            e.printStackTrace();

            stats.put("totalLessons", 0L);
            stats.put("answeredCount", 0L);
            stats.put("correctCount", 0L);
            stats.put("incorrectCount", 0L);
            stats.put("correctPercent", 0.0);
            stats.put("score", 0);
        }

        return stats;
    }
}