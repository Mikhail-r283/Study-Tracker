package com.example.studytracker_1.service;

import com.example.studytracker_1.dto.Lesson;
import com.example.studytracker_1.model.User;
import com.example.studytracker_1.repository.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class LessonGenerationService {

    @Autowired
    private AiService aiService;

    @Autowired
    private LessonRepository lessonRepository;

    public com.example.studytracker_1.model.Lesson generateLesson(String topic, User user) {
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
            Lesson dto = parseJson(jsonResponse);
            System.out.println("✅ [SERVICE] Шаг 2: DTO создан: question=" + dto.getQuestion());

            // 3. Создаём Entity из DTO
            System.out.println("⏳ [SERVICE] Шаг 3: Создание Entity...");
            com.example.studytracker_1.model.Lesson lesson = createLessonFromDto(dto, user);
            System.out.println("✅ [SERVICE] Шаг 3: Entity создан: " + lesson.getQuestion());

            // 4. Считаем очки
            System.out.println("⏳ [SERVICE] Шаг 4: Расчёт очков...");
            lesson.setPoints(calculatePoints(dto.getDifficulty()));
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
        private Lesson parseJson (String json){
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, Lesson.class);
        }

        private com.example.studytracker_1.model.Lesson createLessonFromDto (Lesson dto, User user){
            com.example.studytracker_1.model.Lesson lesson = new com.example.studytracker_1.model.Lesson();
            lesson.setQuestion(dto.getQuestion());
            lesson.setOption1(dto.getOptions().get(0));
            lesson.setOption2(dto.getOptions().get(1));
            lesson.setOption3(dto.getOptions().get(2));
            lesson.setOption4(dto.getOptions().get(3));
            lesson.setCorrectAnswer(dto.getCorrectIndex() + 1);
            lesson.setDescription(dto.getDescription());
            lesson.setDifficulty(dto.getDifficulty() != null ? dto.getDifficulty() : "EASY");
            lesson.setUser(user);
            lesson.setAiGenerated(true);
            lesson.setAnswered(false);
            return lesson;
        }

        private int calculatePoints (String difficulty){
            return switch (difficulty != null ? difficulty.toUpperCase() : "EASY") {
                case "EASY" -> 10;
                case "MEDIUM" -> 20;
                case "HARD" -> 30;
                default -> 10;
            };
        }
    }

