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

        // Получаем пользователя
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Получаем все уроки пользователя
        List<Lesson> userLessons = lessonRepository.findByUserId(user.getId());

        // Считаем статистику
        long totalLessons = userLessons.size();
        long answeredCount = userLessons.stream().filter(Lesson::isAnswered).count();
        long correctCount = userLessons.stream().filter(Lesson::getIsCorrect).count();
        long incorrectCount = answeredCount - correctCount;

        double percent = answeredCount > 0 ? (correctCount * 100.0 / answeredCount) : 0;

        stats.put("totalLessons", totalLessons);
        stats.put("answeredCount", answeredCount);
        stats.put("correctCount", correctCount);
        stats.put("incorrectCount", incorrectCount);
        stats.put("correctPercent", Math.round(percent * 100.0) / 100.0);
        stats.put("score", (int) Math.round(percent));

        return stats;
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        List<Lesson> allLessons = lessonRepository.findAll();

        long totalLessons = allLessons.size();
        long answeredCount = allLessons.stream().filter(Lesson::isAnswered).count();
        long correctCount = allLessons.stream().filter(Lesson::getIsCorrect).count();
        long incorrectCount = answeredCount - correctCount;

        double percent = answeredCount > 0 ? (correctCount * 100.0 / answeredCount) : 0;

        stats.put("totalLessons", totalLessons);
        stats.put("answeredCount", answeredCount);
        stats.put("correctCount", correctCount);
        stats.put("incorrectCount", incorrectCount);
        stats.put("correctPercent", Math.round(percent * 100.0) / 100.0);
        stats.put("score", (int) Math.round(percent));

        return stats;
    }
}