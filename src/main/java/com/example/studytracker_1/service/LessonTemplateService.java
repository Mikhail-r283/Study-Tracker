package com.example.studytracker_1.service;

import com.example.studytracker_1.entity.Tag;
import com.example.studytracker_1.model.Lesson;
import com.example.studytracker_1.model.User;
import com.example.studytracker_1.repository.LessonRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class LessonTemplateService {

    private final LessonRepository lessonRepository;
    private final AiService aiService;
    private final TagService tagService;

    public LessonTemplateService(LessonRepository lessonRepository, AiService aiService, TagService tagService) {
        this.lessonRepository = lessonRepository;
        this.aiService = aiService;
        this.tagService = tagService;
    }

    public void createDefaultLessonsForUser(User user) {
        List<Object[]> lessonData = Arrays.asList(
                // === Оригинальные 5 ===
                new Object[]{"Что такое Java?", "Язык программирования", "База данных", "Операционная система", "Фреймворк", 1, Set.of("Java", "Программирование")},
                new Object[]{"Что такое Spring Boot?", "Игра", "Фреймворк для Java", "База данных", "Язык", 2, Set.of("Spring", "Java", "Фреймворк")},
                new Object[]{"Что такое MVC?", "Model View Controller", "Model View Component", "Module View Controller", "Model Version Control", 1, Set.of("Паттерны", "Архитектура")},
                new Object[]{"Какая аннотация используется для REST контроллера?", "@Controller", "@RestController", "@Service", "@Component", 2, Set.of("Spring", "Аннотации")},
                new Object[]{"Что такое JPA?", "Java Platform API", "Java Persistence API", "Java Programming API", "Java Process API", 2, Set.of("JPA", "Базы данных")},

                // === 20 новых вопросов ===
                new Object[]{"Что такое ООП?", "Объектно-ориентированное программирование", "Операционная обработка программ", "Основы организации процессов", "Объединение операций и процессов", 1, Set.of("ООП", "Парадигмы")},
                new Object[]{"Какое ключевое слово используется для наследования в Java?", "implements", "extends", "inherits", "super", 2, Set.of("Java", "Наследование")},
                new Object[]{"Что такое полиморфизм?", "Способность объекта принимать множество форм", "Множественное наследование", "Перегрузка операторов", "Сборка мусора", 1, Set.of("ООП", "Полиморфизм")},
                new Object[]{"Что такое SQL?", "Structured Query Language", "Simple Query Language", "Standard Query Language", "System Query Language", 1, Set.of("SQL", "Базы данных")},
                new Object[]{"Какая СУБД является самой популярной?", "MongoDB", "PostgreSQL", "MySQL", "SQLite", 3, Set.of("Базы данных", "MySQL")},
                new Object[]{"Что такое Docker?", "Виртуальная машина", "Контейнеризация приложений", "База данных", "Операционная система", 2, Set.of("Docker", "DevOps")},
                new Object[]{"Что такое Git?", "Система управления базами данных", "Система контроля версий", "Система сборки", "Система тестирования", 2, Set.of("Git", "VCS")},
                new Object[]{"Что такое REST API?", "Representational State Transfer", "Remote System Transfer", "Random State Transfer", "Request State Transfer", 1, Set.of("API", "REST", "Веб")},
                new Object[]{"Какая команда создаёт новый репозиторий в Git?", "git new", "git create", "git init", "git start", 3, Set.of("Git", "Команды")},
                new Object[]{"Что такое инкапсуляция?", "Скрытие реализации и защита данных", "Наследование классов", "Переопределение методов", "Создание объектов", 1, Set.of("ООП", "Инкапсуляция")},
                new Object[]{"Что такое ORM?", "Object-Relational Mapping", "Object Request Model", "Object Running Mode", "Object Resource Management", 1, Set.of("ORM", "Базы данных")},
                new Object[]{"Что такое Maven?", "Инструмент сборки проектов", "База данных", "Фреймворк", "Веб-сервер", 1, Set.of("Maven", "Сборка")},
                new Object[]{"Что такое Hibernate?", "Веб-фреймворк", "ORM-фреймворк для Java", "Система сборки", "Тестовый фреймворк", 2, Set.of("Hibernate", "ORM", "Java")},
                new Object[]{"Какая база данных является NoSQL?", "PostgreSQL", "MySQL", "MongoDB", "Oracle", 3, Set.of("NoSQL", "MongoDB")},
                new Object[]{"Что такое Dependency Injection?", "Внедрение зависимостей", "Удаление зависимостей", "Создание зависимостей", "Тестирование зависимостей", 1, Set.of("DI", "Spring")},
                new Object[]{"Что такое HTML?", "HyperText Markup Language", "High Tech Modern Language", "Home Tool Markup Language", "Hyper Transfer Markup Language", 1, Set.of("HTML", "Веб")},
                new Object[]{"Что такое CSS?", "Cascading Style Sheets", "Computer Style Sheets", "Creative Style System", "Color Style Sheets", 1, Set.of("CSS", "Стили")},
                new Object[]{"Что такое JavaScript?", "Язык для серверной части", "Язык для клиентской части", "Язык для баз данных", "Язык для стилей", 2, Set.of("JavaScript", "Фронтенд")},
                new Object[]{"Что такое JSON?", "JavaScript Object Notation", "Java Object Network", "JavaScript Online Network", "Java Standard Object Notation", 1, Set.of("JSON", "Форматы данных")},
                new Object[]{"Что такое алгоритм?", "Последовательность инструкций для решения задачи", "Готовая программа", "Тип данных", "Переменная", 1, Set.of("Алгоритмы", "Основы")}
        );

        // Создаём и сохраняем каждый урок
        for (Object[] data : lessonData) {
            Lesson lesson = createLesson(
                    (String) data[0],    // question
                    (String) data[1],    // option1
                    (String) data[2],    // option2
                    (String) data[3],    // option3
                    (String) data[4],    // option4
                    (int) data[5],       // correctAnswer
                    user,         // user
                    (Set<String>) data[6] // tags
            );

            Lesson saved = lessonRepository.save(lesson);
            System.out.println("✅ Урок #" + saved.getId() + ": " + saved.getQuestion() +
                    " 🏷️ Теги: " + data[6]);
        }

        System.out.println("🎉 Всего создано уроков: " + lessonData.size());
    }

    private Lesson createLesson(String question, String opt1, String opt2, String opt3, String opt4, int correctAnswer, User user, Set<String> datum) {
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
        try {
            String contentForTags = question + " " + opt1; // или только question
            Set<String> tagNames = aiService.detectTags(contentForTags);
            Set<Tag> tags = tagService.findOrCreateTags(tagNames);
            lesson.setTags(tags);
            System.out.println("🏷️ Теги для '" + question + "': " + tagNames);
        } catch (Exception e) {
            System.out.println("⚠️ Не удалось определить теги: " + e.getMessage());
            // Урок создаётся даже без тегов
        }

        return lesson;
    }
    }


