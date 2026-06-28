package com.example.studytracker_1.service;


import com.example.studytracker_1.entity.Tag;
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

import java.util.*;


@Service
public class AiService {


    // Просто создаём RestTemplate прямо здесь
    private final RestTemplate restTemplate = new RestTemplate();

    // URL прямо здесь, никаких @Value
    private final String apiUrl = "http://localhost:11434/api/generate";

    private final String model = "ministral-3";

    public String generate(String topic) {
        System.out.println("🤖🤖🤖 AiService.generate вызван с темой: " + topic);
        String prompt = String.format("""
        Сгенерируй учебный урок по теме: '%s'.
        
        Ответ строго в JSON без markdown:
        {
          "title": "...",
          "description": "...",
          "difficulty": "EASY/MEDIUM/HARD",
          "questions": [
            {
              "question": "Вопрос?",
              "options": ["вар1", "вар2", "вар3", "вар4"],
              "correctIndex": 0,
              "description": "Объяснение"
            }
          ]
        }
        
        Сгенерируй 3-5 вопросов.
        """, topic);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("prompt", prompt);
        requestBody.put("stream", false);
        requestBody.put("temperature", 0.7);

        String response = restTemplate.postForObject(apiUrl, requestBody, String.class);

        // Парсим и возвращаем только текст ответа
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);
        String content = root.get("response").asText().trim();

        // Чистим от markdown
        if (content.startsWith("```")) {
            content = content.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
        }

        return content; // Сырой JSON строкой
    }

    public Set<String> detectTags(String text) {
        Set<String> tags = new HashSet<>();
        if (text == null || text.isBlank()) return tags;

        String lowerText = text.toLowerCase();

        Map<String, List<String>> tagKeywords = new LinkedHashMap<>();
        tagKeywords.put("Java", Arrays.asList("java", "джава", "spring", "hibernate"));
        tagKeywords.put("Python", Arrays.asList("python", "питон"));
        tagKeywords.put("JavaScript", Arrays.asList("javascript", "js", "react", "vue"));
        tagKeywords.put("База данных", Arrays.asList("база данных", "sql", "database", "postgresql"));
        tagKeywords.put("Алгоритмы", Arrays.asList("алгоритм", "сортировк", "поиск"));
        tagKeywords.put("Математика", Arrays.asList("математик", "формул", "уравнен", "числ"));
        tagKeywords.put("Физика", Arrays.asList("физик", "закон", "энерги", "сил"));
        tagKeywords.put("Химия", Arrays.asList("хими", "реакци", "молекул", "атом"));
        tagKeywords.put("Английский", Arrays.asList("english", "английск", "vocabulary", "grammar"));
        tagKeywords.put("История", Arrays.asList("истори", "войн", "импери", "революци"));
        tagKeywords.put("Дизайн", Arrays.asList("дизайн", "график", "цвет", "шрифт"));
        tagKeywords.put("Музыка", Arrays.asList("музык", "нот", "аккорд", "мелоди"));
        tagKeywords.put("Спорт", Arrays.asList("спорт", "трениров", "упражнен", "физическ", "бег"));

        for (Map.Entry<String, List<String>> entry : tagKeywords.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (lowerText.contains(keyword)) {
                    tags.add(entry.getKey());
                    break;
                }
            }
        }

        if (tags.isEmpty()) {
            tags.add("Общее");
        }

        return tags;
    }

}
