package com.example.studytracker_1.service;


import com.example.studytracker_1.model.Lesson;
import com.example.studytracker_1.model.User;
import com.example.studytracker_1.repository.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@Service
public class AiService {

    @Autowired
    private LessonRepository lessonRepository;

    // Просто создаём RestTemplate прямо здесь
    private final RestTemplate restTemplate = new RestTemplate();

    // URL прямо здесь, никаких @Value
    private final String apiUrl = "http://localhost:11434/api/generate";

    private final String model = "ministral-3";

    public Lesson generateLesson(String topic, User user) {
        System.out.println("🚀 ===== НАЧАЛО ГЕНЕРАЦИИ =====");
        System.out.println("📝 Тема: " + topic);
        System.out.println("🔗 URL: " + apiUrl);
        System.out.println("🤖 Модель: " + model);

        String response = null; // ← объявляем ДО try

        try {
            String prompt = String.format(
                    "Сгенерируй учебный вопрос по теме: '%s'. " +
                            "Ответ должен быть строго в формате JSON, без лишнего текста:\n" +
                            "{\n" +
                            "  \"question\": \"Вопрос по теме\",\n" +
                            "  \"options\": [\"Вариант 1\", \"Вариант 2\", \"Вариант 3\", \"Вариант 4\"],\n" +
                            "  \"correctIndex\": 0,\n" +
                            "  \"description\": \"Краткое объяснение правильного ответа\"\n" +
                            "}\n" +
                            "correctIndex — это индекс правильного ответа (0-3). " +
                            "description — объяснение (не более 200 символов).",
                    topic
            );

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("prompt", prompt);
            requestBody.put("stream", false);
            requestBody.put("temperature", 0.7);

            System.out.println("📤 Отправляем запрос в Ollama...");
            response = restTemplate.postForObject(apiUrl, requestBody, String.class);
            System.out.println("📥 Ответ получен");

            // Парсим ответ
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            String content = root.get("response").asText().trim();

            System.out.println("📄 Содержимое ответа: " + content);

            // Убираем markdown-обёртку, если есть
            if (content.startsWith("```")) {
                content = content.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
            }

            // Парсим JSON
            JsonNode lessonData = mapper.readTree(content);

            // Создаём урок
            Lesson lesson = new Lesson();
            lesson.setQuestion(lessonData.get("question").asText());
            lesson.setOption1(lessonData.get("options").get(0).asText());
            lesson.setOption2(lessonData.get("options").get(1).asText());
            lesson.setOption3(lessonData.get("options").get(2).asText());
            lesson.setOption4(lessonData.get("options").get(3).asText());
            lesson.setCorrectAnswer(lessonData.get("correctIndex").asInt() + 1);
            lesson.setDescription(lessonData.has("description") ?
                    lessonData.get("description").asText() : "");
            lesson.setDifficulty("EASY");
            lesson.setPoints(10);
            lesson.setUser(user);
            lesson.setAiGenerated(true);
            lesson.setAnswered(false);

            Lesson saved = lessonRepository.save(lesson);
            System.out.println("✅ Урок сохранён! ID: " + saved.getId() +
                    ", Вопрос: " + saved.getQuestion());

            return saved;

        } catch (Exception e) {
            System.out.println("❌ Ошибка генерации: " + e.getMessage());
            e.printStackTrace();

            // fallback
            Lesson fallback = new Lesson();
            fallback.setQuestion(topic);
            fallback.setDescription("Не удалось сгенерировать урок через AI");
            fallback.setOption1("Повторить попытку");
            fallback.setOption2("Выбрать другую тему");
            fallback.setOption3("Обратиться к учителю");
            fallback.setOption4("Искать в учебнике");
            fallback.setCorrectAnswer(1);
            fallback.setDifficulty("EASY");
            fallback.setPoints(5);
            fallback.setUser(user);
            fallback.setAiGenerated(false);
            fallback.setAnswered(false);
            return lessonRepository.save(fallback);
        }
    }
}
