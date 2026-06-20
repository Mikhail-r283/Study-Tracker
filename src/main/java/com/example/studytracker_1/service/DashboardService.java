package com.example.studytracker_1.service;

import com.example.studytracker_1.model.Statistics;
import com.example.studytracker_1.repository.StatisticsRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DashboardService {

    private final StatisticsRepository statisticsRepository;

    public DashboardService(StatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        Optional<Statistics> currentStats = statisticsRepository.findById(1L);

        if (currentStats.isPresent()) {
            Statistics s = currentStats.get();
            stats.put("totalLessons", s.getTotalLessons());
            stats.put("answeredCount", s.getAnsweredCount());
            stats.put("correctCount", s.getCorrectCount());
            stats.put("incorrectCount", s.getIncorrectCount());
            stats.put("score", s.getScore());
        } else {
            stats.put("totalLessons", 0);
            stats.put("answeredCount", 0);
            stats.put("correctCount", 0);
            stats.put("incorrectCount", 0);
            stats.put("score", 0);
        }

        int answered = (int) stats.get("answeredCount");
        int correct = (int) stats.get("correctCount");
        double percent = answered > 0 ? (correct * 100.0 / answered) : 0;
        stats.put("correctPercent", Math.round(percent * 100.0) / 100.0);

        return stats;
    }
}