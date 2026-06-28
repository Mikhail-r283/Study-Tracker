package com.example.studytracker_1.service;

import com.example.studytracker_1.model.Lesson;
import com.example.studytracker_1.model.Question;
import com.example.studytracker_1.model.Statistics;
import com.example.studytracker_1.model.User;
import com.example.studytracker_1.repository.LessonRepository;
import com.example.studytracker_1.repository.QuestionRepository;
import com.example.studytracker_1.repository.StatisticsRepository;
import com.example.studytracker_1.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class DashboardService {
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;

    public DashboardService(LessonRepository lessonRepository,
                            UserRepository userRepository, QuestionRepository questionRepository) {
        this.lessonRepository = lessonRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
    }

    public Map<String, Object> getStatsForUser(String username) {
        Map<String, Object> stats = new HashMap<>();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<Lesson> userLessons = lessonRepository.findByUserId(user.getId());
        List<Long> lessonIds = userLessons.stream().map(Lesson::getId).collect(Collectors.toList());

        // Явный подсчёт через репозиторий
        long totalQuestions = questionRepository.countByLessonIdIn(lessonIds);
        long answeredQuestions = questionRepository.countByLessonIdInAndAnswered(lessonIds, true);
        long correctQuestions = questionRepository.countByLessonIdInAndIsCorrect(lessonIds, true);

        long incorrectQuestions = answeredQuestions - correctQuestions;
        if (incorrectQuestions < 0) incorrectQuestions = 0;

        double percent = answeredQuestions > 0
                ? (correctQuestions * 100.0 / answeredQuestions)
                : 0;

        stats.put("totalLessons", (long) userLessons.size());
        stats.put("answeredLessons", userLessons.stream().filter(Lesson::getAnswered).count());
        stats.put("unansweredLessons", userLessons.stream().filter(l -> !Boolean.TRUE.equals(l.getAnswered())).count());

        stats.put("totalQuestions", totalQuestions);
        stats.put("answeredQuestions", answeredQuestions);
        stats.put("correctQuestions", correctQuestions);
        stats.put("incorrectQuestions", incorrectQuestions);
        stats.put("correctPercent", Math.round(percent * 100.0) / 100.0);
        stats.put("score", (int) Math.round(percent));

        return stats;
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            List<Lesson> allLessons = lessonRepository.findAll();

            // Статистика по урокам
            long totalLessons = allLessons.size();
            long answeredLessons = allLessons.stream()
                    .filter(lesson -> Boolean.TRUE.equals(lesson.getAnswered()))
                    .count();

            // Статистика по вопросам
            List<Question> allQuestions = allLessons.stream()
                    .flatMap(lesson -> lesson.getQuestions().stream())
                    .collect(Collectors.toList());

            long totalQuestions = allQuestions.size();
            long answeredQuestions = allQuestions.stream()
                    .filter(q -> Boolean.TRUE.equals(q.getAnswered()))
                    .count();
            long correctQuestions = allQuestions.stream()
                    .filter(q -> Boolean.TRUE.equals(q.getIsCorrect()))
                    .count();
            long incorrectQuestions = answeredQuestions - correctQuestions;
            if (incorrectQuestions < 0) incorrectQuestions = 0;

            double percent = answeredQuestions > 0
                    ? (correctQuestions * 100.0 / answeredQuestions)
                    : 0;

            stats.put("totalLessons", totalLessons);
            stats.put("answeredLessons", answeredLessons);
            stats.put("unansweredLessons", totalLessons - answeredLessons);
            stats.put("totalQuestions", totalQuestions);
            stats.put("answeredQuestions", answeredQuestions);
            stats.put("correctQuestions", correctQuestions);
            stats.put("incorrectQuestions", incorrectQuestions);
            stats.put("correctPercent", Math.round(percent * 100.0) / 100.0);
            stats.put("score", (int) Math.round(percent));

            System.out.println("✅ Общая статистика: уроков=" + totalLessons +
                    ", вопросов=" + totalQuestions);

        } catch (Exception e) {
            System.out.println("❌ Ошибка в getStats: " + e.getMessage());
            e.printStackTrace();
            stats.put("totalLessons", 0L);
            stats.put("answeredLessons", 0L);
            stats.put("unansweredLessons", 0L);
            stats.put("totalQuestions", 0L);
            stats.put("answeredQuestions", 0L);
            stats.put("correctQuestions", 0L);
            stats.put("incorrectQuestions", 0L);
            stats.put("correctPercent", 0.0);
            stats.put("score", 0);
        }
        return stats;
    }
}
