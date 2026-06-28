package com.example.studytracker_1.service;

import com.example.studytracker_1.dto.LessonDto;
import com.example.studytracker_1.dto.QuestionDto;
import com.example.studytracker_1.model.Difficulty;
import com.example.studytracker_1.model.Lesson;
import com.example.studytracker_1.model.Question;
import com.example.studytracker_1.model.User;
import com.example.studytracker_1.repository.LessonRepository;
import com.example.studytracker_1.repository.QuestionRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

@Service
public class LessonGenerationService {

    @Autowired
    private AiService aiService;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private QuestionRepository questionRepository; // Добавь, если есть

    public Lesson generateLesson(String topic, User user) {
        System.out.println("🚀 [SERVICE] НАЧАЛО generateLesson для темы: " + topic);
        System.out.println("🚀 [SERVICE] Пользователь: " + user.getUsername());

        try {
            // 1. Получаем сырой JSON от нейросети
            System.out.println("⏳ [SERVICE] Шаг 1: Запрашиваю JSON от AiService...");
            String jsonResponse = aiService.generate(topic);
            System.out.println("✅ [SERVICE] Шаг 1: Получен ответ от AI длиной: " + jsonResponse.length());
            System.out.println("📄 [SERVICE] Содержимое: " + jsonResponse);

            // 2. Парсим JSON в объект
            System.out.println("⏳ [SERVICE] Шаг 2: Парсинг JSON...");
            LessonDto dto = parseJson(jsonResponse);
            System.out.println("✅ [SERVICE] Шаг 2: DTO создан, вопросов: " + dto.getQuestions().size());

            // 3. Создаём Entity из DTO
            System.out.println("⏳ [SERVICE] Шаг 3: Создание Entity...");
            com.example.studytracker_1.model.Lesson lesson = createLessonFromDto(dto, user);
            System.out.println("✅ [SERVICE] Шаг 3: Entity создан: " + lesson.getTitle());

            // 4. Считаем очки
            System.out.println("⏳ [SERVICE] Шаг 4: Расчёт очков...");
            lesson.setPoints(calculatePoints(dto.getDifficulty()));
            lesson.setTotalPoints(dto.getQuestions().size() * calculatePoints(dto.getDifficulty()));
            System.out.println("✅ [SERVICE] Шаг 4: Очки = " + lesson.getPoints());

            // 5. Сохраняем
            System.out.println("⏳ [SERVICE] Шаг 5: Сохранение в БД...");
            com.example.studytracker_1.model.Lesson savedLesson = lessonRepository.save(lesson);
            System.out.println("✅ [SERVICE] Шаг 5: Урок сохранён с ID: " + savedLesson.getId());

            return savedLesson;

        } catch (Exception e) {
            System.err.println("❌ [SERVICE] Ошибка: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Не удалось сгенерировать урок", e);
        }
    }

    // 👇 Вспомогательные методы — только внутри этого сервиса
    private LessonDto parseJson(String json) {
        System.out.println("=== ПАРСИНГ JSON ===");
        System.out.println("JSON для парсинга: " + json);

        try {
            ObjectMapper mapper = new ObjectMapper();

            LessonDto dto = mapper.readValue(json, LessonDto.class);
            System.out.println("✅ Урок: " + dto.getTitle());
            System.out.println("✅ Вопросов: " + dto.getQuestions().size());

            return dto;
        } catch (Exception e) {
            System.out.println("❌ Ошибка парсинга: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Не удалось распарсить JSON: " + e.getMessage());
        }
    }

    private Lesson createLessonFromDto(LessonDto dto, User user) {
        com.example.studytracker_1.model.Lesson lesson = new com.example.studytracker_1.model.Lesson();

        lesson.setTitle(dto.getTitle());
        lesson.setDescription(dto.getDescription());
        lesson.setDifficulty(Difficulty.valueOf(dto.getDifficulty()));
        lesson.setUser(user);
        lesson.setAnswered(false);

        // Создаём вопросы
        List<Question> questions = new ArrayList<>();
        for (QuestionDto qDto : dto.getQuestions()) {
            Question question = new Question();
            question.setQuestion(qDto.getQuestion());
            question.setOption1(qDto.getOptions().get(0));
            question.setOption2(qDto.getOptions().get(1));
            question.setOption3(qDto.getOptions().get(2));
            question.setOption4(qDto.getOptions().get(3));
            question.setCorrectAnswer(qDto.getCorrectIndex());
            question.setLesson(lesson);
            questions.add(question);
        }
        lesson.setQuestions(questions);

        return lesson;
    }

    private int calculatePoints(String difficulty) {
        return switch (difficulty != null ? difficulty.toUpperCase() : "EASY") {
            case "EASY" -> 10;
            case "MEDIUM" -> 20;
            case "HARD" -> 30;
            default -> 10;
        };
    }
}

