package com.example.studytracker_1.service;

import com.example.studytracker_1.config.LessonData;
import com.example.studytracker_1.dto.LessonTemplateDto;
import com.example.studytracker_1.dto.QuestionTemplateDto;
import com.example.studytracker_1.entity.Tag;
import com.example.studytracker_1.model.Difficulty;
import com.example.studytracker_1.model.Lesson;
import com.example.studytracker_1.model.Question;
import com.example.studytracker_1.model.User;
import com.example.studytracker_1.repository.LessonRepository;
import com.example.studytracker_1.repository.QuestionRepository;
import com.example.studytracker_1.repository.TagRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class LessonService {

    private final LessonData lessonData;
    private final TagRepository tagRepository;
    private final LessonRepository lessonRepository;
    private final QuestionRepository questionRepository;

    public LessonService(LessonData lessonData, TagRepository tagRepository,
                         LessonRepository lessonRepository,
                         QuestionRepository questionRepository) {
        this.lessonData = lessonData;
        this.tagRepository = tagRepository;
        this.lessonRepository = lessonRepository;
        this.questionRepository = questionRepository;
    }

    // ========== ГЕНЕРАЦИЯ СЛУЧАЙНОГО УРОКА ==========

    public Lesson generateRandomLesson(User user) {
        // Получаем список всех уроков
        List<LessonTemplateDto> templates = lessonData.getAllLessonTemplates();

        // Выбираем первый урок
        Random random = new Random();
        LessonTemplateDto selectedTemplate = templates.get(random.nextInt(templates.size()));

        Lesson lesson = new Lesson();
        lesson.setTitle(selectedTemplate.getTitle());
        lesson.setDescription(selectedTemplate.getDescription());
        lesson.setDifficulty(selectedTemplate.getDifficulty());
        lesson.setUser(user);
        lesson.setAnswered(false);
        lesson.setPoints(0);
        lesson.setTotalPoints(0);

        // Создаём вопросы с номерами
        List<Question> questions = new ArrayList<>();
        int questionNumber = 1;

        for (QuestionTemplateDto template : selectedTemplate.getQuestionTemplateDtos()) {
            Question q = createQuestionFromTemplate(template, lesson);
            q.setQuestionNumber(questionNumber++);
            questions.add(q);
        }

        lesson.setQuestions(questions);

        // Сохраняем урок (вопросы сохранятся каскадно)
        Lesson savedLesson = lessonRepository.save(lesson);

        System.out.println("Создан урок с " + savedLesson.getQuestions().size() + " вопросами");
        for (Question q : savedLesson.getQuestions()) {
            System.out.println("  Вопрос ID=" + q.getId() + ": questionNumber=" + q.getQuestionNumber() + " : " + q.getQuestion());
        }

        return savedLesson;
    }

//    public List<Lesson> generateRandomLessons(int count, User user) {
//        return IntStream.range(0, count)
//                .mapToObj(i -> generateRandomLesson(user))
//                .collect(Collectors.toList());
//    }

    private Question createQuestionFromTemplate(QuestionTemplateDto template, Lesson lesson) {
        Question question = new Question();
        question.setQuestion(template.getQuestion());
        question.setOption1(template.getOption1());
        question.setOption2(template.getOption2());
        question.setOption3(template.getOption3());
        question.setOption4(template.getOption4());
        question.setCorrectAnswer(template.getCorrectAnswer());
        question.setPoints(template.getPoints());
        question.setLesson(lesson);
        return question;
    }

    public Lesson findById(Long id) {
        return lessonRepository.findById(id).get();
    }

    @Transactional
    public void update(Lesson lesson) {
        if (lesson.getId() == null) {
            throw new IllegalArgumentException("Нельзя обновить урок без ID");
        }
        lessonRepository.save(lesson);
    }

    public void deleteLesson(Long lessonId, Long userId) {
        lessonRepository.deleteByIdOrUserIdNull(lessonId, userId);
    }

    public void deleteById(Long id) {
        lessonRepository.deleteById(id);
    }

    @Transactional
    public void deleteByIdAndUser(Long lessonId, Long userId) {
        // Сначала удаляем связи из lesson_tags
        lessonRepository.deleteLessonTagsByLessonId(lessonId);
        // Потом удаляем сам урок
        lessonRepository.deleteByIdOrUserIdNull(lessonId, userId);
    }

    public void resetLessonProgress(Long lessonId) {
        Lesson lesson = findById(lessonId);

        // Сбросить все вопросы
        List<Question> questions = questionRepository.findByLessonId(lessonId);
        for (Question q : questions) {
            q.setAnswered(false);
            q.setIsCorrect(null);
            questionRepository.save(q);
        }

        // Сбросить урок
        lesson.setPoints(0);
        lesson.setAnswered(false);
        update(lesson);
    }

    public Lesson save(Lesson lesson) {
        return lessonRepository.save(lesson);
    }

    public Lesson getLessonById(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + id));
    }

    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    public void deleteLesson(Long id) {
        lessonRepository.deleteById(id);
    }
}