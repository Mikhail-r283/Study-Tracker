package com.example.studytracker_1.service;

import com.example.studytracker_1.model.Lesson;
import com.example.studytracker_1.repository.LessonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;
    private final StatisticsService statisticsService;

    public LessonService(LessonRepository lessonRepository,
                         StatisticsService statisticsService) {
        this.lessonRepository = lessonRepository;
        this.statisticsService = statisticsService;
    }

    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    public Lesson getLessonById(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found: " + id));
    }

    @Transactional
    public boolean checkAnswer(Long lessonId, int selectedAnswer) {
        Lesson lesson = getLessonById(lessonId);

        if (lesson.isAnswered()) {
            return lesson.getIsCorrect();
        }

        boolean isCorrect = selectedAnswer == lesson.getCorrectAnswer();
        lesson.setAnswered(true);
        lesson.setIsCorrect(isCorrect);
        lessonRepository.save(lesson);

        // Обновляем статистику
        statisticsService.updateStatistics(isCorrect);

        return isCorrect;
    }
    @Transactional
    public void resetLessons() {
        List<Lesson> lessons = lessonRepository.findAll();
        for (Lesson lesson : lessons) {
            lesson.setAnswered(false);
            lesson.setIsCorrect(null);
        }
        lessonRepository.saveAll(lessons);
    }
}
