package com.example.studytracker_1.service;

import com.example.studytracker_1.model.Lesson;
import com.example.studytracker_1.model.User;
import com.example.studytracker_1.repository.LessonRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class LessonTemplateService {

    private final LessonRepository lessonRepository;

    public LessonTemplateService(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    public void createDefaultLessonsForUser(User user) {
        List<Lesson> defaultLessons = Arrays.asList(
                createLesson("Сколько будет 2+2?", "3", "4", "5", "6", 2, user),
                createLesson("Столица Франции?", "Лондон", "Берлин", "Париж", "Мадрид", 3, user),
                createLesson("Сколько дней в неделе?", "5", "6", "7", "8", 3, user),
                createLesson("Какой газ мы вдыхаем?", "Углекислый", "Кислород", "Водород", "Азот", 2, user),
                createLesson("Сколько месяцев в году?", "10", "11", "12", "13", 3, user)
        );

        lessonRepository.saveAll(defaultLessons);
    }

    private Lesson createLesson(String question, String opt1, String opt2, String opt3, String opt4, int correctAnswer, User user) {
        Lesson lesson = new Lesson();
        lesson.setQuestion(question);
        lesson.setOption1(opt1);
        lesson.setOption2(opt2);
        lesson.setOption3(opt3);
        lesson.setOption4(opt4);
        lesson.setCorrectAnswer(correctAnswer);
        lesson.setAnswered(false);
        lesson.setIsCorrect(false);
        lesson.setUser(user);
        return lesson;
    }
}

