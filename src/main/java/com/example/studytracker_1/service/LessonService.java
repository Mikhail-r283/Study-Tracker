package com.example.studytracker_1.service;

import com.example.studytracker_1.model.Lesson;
import com.example.studytracker_1.model.User;
import com.example.studytracker_1.repository.LessonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;

    public LessonService(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    public List<Lesson> getLessonsByUser(User user) {
        return lessonRepository.findByUser(user);
    }

    public Lesson getLessonById(Long id) {
        return lessonRepository.findById(id).orElse(null);
    }

    public void saveLesson(Lesson lesson) {
        lessonRepository.save(lesson);
    }
}
