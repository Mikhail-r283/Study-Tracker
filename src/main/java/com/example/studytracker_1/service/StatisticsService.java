package com.example.studytracker_1.service;

import com.example.studytracker_1.model.Statistics;
import com.example.studytracker_1.repository.StatisticsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;

    public StatisticsService(StatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    @Transactional
    public void updateStatistics(boolean isCorrect) {
        Statistics stats = statisticsRepository.findById(1L)
                .orElseGet(() -> {
                    Statistics newStats = new Statistics();
                    newStats.setTotalLessons(countAllLessons());
                    return newStats;
                });

        stats.setAnsweredCount(stats.getAnsweredCount() + 1);

        if (isCorrect) {
            stats.setCorrectCount(stats.getCorrectCount() + 1);
            stats.setScore(stats.getScore() + 10);
        } else {
            stats.setIncorrectCount(stats.getIncorrectCount() + 1);
        }

        statisticsRepository.save(stats);
    }

    private int countAllLessons() {
        return 0; // Заглушка, можно внедрить LessonRepository
    }
}