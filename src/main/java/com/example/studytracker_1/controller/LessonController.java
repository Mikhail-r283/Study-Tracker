package com.example.studytracker_1.controller;

import com.example.studytracker_1.dto.AnswerRequest;
import com.example.studytracker_1.entity.Tag;
import com.example.studytracker_1.model.Lesson;
import com.example.studytracker_1.model.Question;
import com.example.studytracker_1.model.User;
import com.example.studytracker_1.repository.LessonRepository;
import com.example.studytracker_1.repository.QuestionRepository;
import com.example.studytracker_1.repository.UserRepository;
import com.example.studytracker_1.service.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    private final LessonRepository lessonRepository;
    private final LessonService lessonService;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final LessonGenerationService lessonGenerationService;

    public LessonController(LessonRepository lessonRepository, LessonService lessonService,
                            QuestionRepository questionRepository,
                            UserRepository userRepository,
                            LessonGenerationService lessonGenerationService
    ) {
        this.lessonRepository = lessonRepository;
        this.lessonService = lessonService;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.lessonGenerationService = lessonGenerationService;
    }
    // 1. Получить все уроки пользователя

    @GetMapping("lessons")
    public String testLessons(Model model) {
        model.addAttribute("lessons", List.of());
        return "lessons";  // Имя вашего шаблона
    }

    @GetMapping
    public String getUserLessons(@AuthenticationPrincipal UserDetails userDetails,
                                 Model model,
                                 @RequestParam(required = false) String success,
                                 @RequestParam(required = false) String error) {

        // НЕ делаем редирект, просто показываем пустой список
        if (userDetails == null) {
            System.out.println("User not authenticated - showing empty list");
            model.addAttribute("lessons", List.of());
            return "lessons"; // Просто покажи страницу без уроков
        }

        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);

        if (user != null) {
            List<Lesson> lessons = lessonRepository.findByUserId(user.getId());
            System.out.println("Found lessons: " + lessons.size());
            model.addAttribute("lessons", lessons);
        } else {
            System.out.println("User not found");
            model.addAttribute("lessons", List.of());
        }

        // Сообщения
        if ("generated".equals(success)) {
            model.addAttribute("successMessage", "Случайный урок успешно создан!");
        }
        if (error != null) {
            model.addAttribute("errorMessage", "Произошла ошибка");
        }

        return "lessons";
    }

    // 2. Создать урок из шаблонов
    @PostMapping("/templates")
    public ResponseEntity<Map<String, String>> createTemplateLessons(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Map<String, String> response = new HashMap<>();
        response.put("message", "Шаблонные уроки успешно созданы!");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    // 3. Удалить все уроки пользователя
    @DeleteMapping("/all")
    public ResponseEntity<Map<String, String>> deleteAllUserLessons(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<Lesson> lessons = lessonRepository.findByUserId(user.getId());

        // Сначала удаляем все вопросы, потом уроки
        for (Lesson lesson : lessons) {
            questionRepository.deleteAll(lesson.getQuestions());
        }
        lessonRepository.deleteAll(lessons);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Все уроки пользователя удалены");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }
    // 4. Генерация урока через AI
   /* @PostMapping("/generate")
    public ResponseEntity<LessonResponse> generateLesson(
            @RequestParam String topic,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Lesson lesson = lessonGenerationService.generateLesson(topic, user);
        LessonResponse response = convertToResponse(lesson);

        return ResponseEntity.ok(response);
    } */





    // 7. Получить статистику по уроку
    @GetMapping("/{lessonId}/stats")
    public ResponseEntity<LessonStats> getLessonStats(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Урок не найден"));

        if (!lesson.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Question> questions = lesson.getQuestions();
        long totalQuestions = questions.size();
        long answeredQuestions = questions.stream()
                .filter(q -> Boolean.TRUE.equals(q.getAnswered()))
                .count();
        long correctAnswers = questions.stream()
                .filter(q -> Boolean.TRUE.equals(q.getIsCorrect()))
                .count();
        int totalPoints = questions.stream()
                .filter(q -> Boolean.TRUE.equals(q.getIsCorrect()))
                .mapToInt(q -> q.getPoints() != null ? q.getPoints() : 0)
                .sum();

        LessonStats stats = new LessonStats();
        stats.setTotalQuestions((int) totalQuestions);
        stats.setAnsweredQuestions((int) answeredQuestions);
        stats.setCorrectAnswers((int) correctAnswers);
        stats.setTotalPoints(totalPoints);
        stats.setProgress((int) (totalQuestions > 0 ? (answeredQuestions * 100 / totalQuestions) : 0));
        stats.setAccuracy((int) (answeredQuestions > 0 ? (correctAnswers * 100 / answeredQuestions) : 0));

        return ResponseEntity.ok(stats);
    }

    // 8. Получить все теги урока
    @GetMapping("/{lessonId}/tags")
    public ResponseEntity<Set<TagResponse>> getLessonTags(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Урок не найден"));

        if (!lesson.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Set<TagResponse> tags = lesson.getQuestions().stream()
                .flatMap(q -> q.getTags().stream())
                .distinct()
                .map(tag -> new TagResponse(tag.getId(), tag.getName(), tag.getIconUrl()))
                .collect(Collectors.toSet());

        return ResponseEntity.ok(tags);
    }

    // Вспомогательные методы
      private LessonResponse convertToResponse(Lesson lesson) {
        LessonResponse response = new LessonResponse();
        response.setId(lesson.getId());
        response.setQuestion(lesson.getTitle());  // <-- ИЗМЕНЕНО: title -> question
        response.setDescription(lesson.getDescription());
        response.setDifficulty(lesson.getDifficulty().toString());
        response.setPoints(lesson.getPoints());
        response.setTotalPoints(lesson.getTotalPoints());
        response.setAiGenerated(lesson.getAiGenerated());
        response.setAnswered(lesson.getAnswered() != null ? lesson.getAnswered() : false);  // <-- ИЗМЕНЕНО: гарантируем boolean

        // Теги урока (из вопросов)
        if (lesson.getQuestions() != null) {
            Set<TagResponse> uniqueTags = new HashSet<>();
            for (Question q : lesson.getQuestions()) {
                if (q.getTags() != null) {
                    for (Tag tag : q.getTags()) {
                        uniqueTags.add(new TagResponse(tag.getId(), tag.getName(), tag.getIconUrl()));
                    }
                }
            }
            response.setTags(new ArrayList<>(uniqueTags));
        }
        // Конвертируем вопросы
        if (lesson.getQuestions() != null) {
            List<QuestionResponse> questionResponses = lesson.getQuestions().stream()
                    .map(q -> {
                        QuestionResponse qr = new QuestionResponse();
                        qr.setId(q.getId());
                        qr.setQuestionNumber(q.getQuestionNumber());
                        qr.setQuestion(q.getQuestion());
                        qr.setOptions(q.getOptionsList());
                        qr.setPoints(q.getPoints());
                        qr.setAnswered(q.getAnswered() != null ? q.getAnswered() : false);  // <-- ИЗМЕНЕНО
                        qr.setIsCorrect(q.getIsCorrect());
                        qr.setDifficulty(q.getDifficulty());
                        qr.setHint(q.getHint());
                        qr.setExplanation(q.getExplanation());

                        // Теги вопроса
                        if (q.getTags() != null) {
                            qr.setTags(q.getTags().stream()
                                    .map(tag -> new TagResponse(tag.getId(), tag.getName(), tag.getIconUrl()))
                                    .collect(Collectors.toSet()));
                        }

                        // Показываем ответы только если вопрос отвечен
                        if (Boolean.TRUE.equals(q.getAnswered())) {
                            qr.setCorrectAnswer(q.getCorrectAnswer());
                            qr.setUserAnswer(q.getUserAnswer());
                            qr.setCorrectAnswerText(q.getCorrectAnswerText());
                            qr.setUserAnswerText(q.getUserAnswerText());
                        }

                        return qr;
                    })
                    .collect(Collectors.toList());
            response.setQuestions(questionResponses);
        }

        return response;
    }


    private void updateLessonProgress(Lesson lesson) {
        List<Question> questions = lesson.getQuestions();

        boolean allAnswered = questions.stream()
                .allMatch(q -> Boolean.TRUE.equals(q.getAnswered()));

        lesson.setAnswered(allAnswered);

        // Обновляем общие очки
        int totalEarned = questions.stream()
                .filter(q -> Boolean.TRUE.equals(q.getIsCorrect()))
                .mapToInt(q -> q.getPoints() != null ? q.getPoints() : 0)
                .sum();
        lesson.setPoints(totalEarned);

        lessonRepository.save(lesson);
    }

  /*  @PostMapping("/generate/random")
    public String generateRandomLesson(Principal principal, RedirectAttributes redirectAttributes) {
        try {
            // 1. Проверяем пользователя
            if (principal == null || principal.getName() == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Пользователь не авторизован");
                return "redirect:/login";
            }

            // 2. Получаем пользователя
            Optional<User> currentUser = userRepository.findByUsername(principal.getName());
            if (currentUser.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Пользователь не найден");
                return "redirect:/lessons";
            }

            // 3. Генерируем случайный урок (включая вопросы)
            Lesson lesson = lessonService.generateRandomLesson();
            System.out.println(">>> Создан урок: " + lesson.getTitle() + " с ID: " + lesson.getId());

            // 4. Привязываем к пользователю
            lesson.setUser(currentUser.get());

            // 5. Сохраняем
            lessonService.save(lesson);
            System.out.println(">>> Урок сохранён. ID: " + lesson.getId());

            // 6. Проверяем, что урок действительно сохранился
            Lesson savedLesson = lessonService.getLessonById(lesson.getId());
            System.out.println(">>> Проверка: найден урок = " + (savedLesson != null));

            redirectAttributes.addFlashAttribute("successMessage", "Случайный урок '" + lesson.getTitle() + "' успешно создан!");

            return "redirect:/lessons";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при создании урока: " + e.getMessage());
            return "redirect:/lessons";
        }
    }

*/

    // =============== DTO классы ===============

    static class LessonResponse {
        private Long id;
        private String question;      // <-- ИЗМЕНЕНО: вместо title
        private String description;
        private String difficulty;
        private Integer points;
        private Integer totalPoints;
        private Boolean aiGenerated;
        private Boolean answered;
        private LocalDateTime createdAt;
        private List<QuestionResponse> questions;
        private List<TagResponse> tags;  // <-- ДОБАВЛЕНО

        // Геттеры и сеттеры
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getDifficulty() { return difficulty; }
        public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
        public Integer getPoints() { return points; }
        public void setPoints(Integer points) { this.points = points; }
        public Integer getTotalPoints() { return totalPoints; }
        public void setTotalPoints(Integer totalPoints) { this.totalPoints = totalPoints; }
        public Boolean getAiGenerated() { return aiGenerated; }
        public void setAiGenerated(Boolean aiGenerated) { this.aiGenerated = aiGenerated; }
        public Boolean getAnswered() { return answered; }
        public void setAnswered(Boolean answered) { this.answered = answered; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public List<QuestionResponse> getQuestions() { return questions; }
        public void setQuestions(List<QuestionResponse> questions) { this.questions = questions; }
        public List<TagResponse> getTags() { return tags; }
        public void setTags(List<TagResponse> tags) { this.tags = tags; }
    }

    static class QuestionResponse {
        private Long id;
        private int questionNumber;
        private String question;
        private List<String> options;
        private Integer points;
        private Boolean answered;
        private Boolean isCorrect;
        private String difficulty;
        private String hint;
        private String explanation;
        private Set<TagResponse> tags;

        // Показываются только если вопрос отвечен
        private Integer correctAnswer;
        private Integer userAnswer;
        private String correctAnswerText;
        private String userAnswerText;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public int getQuestionNumber() { return questionNumber; }
        public void setQuestionNumber(int questionNumber) { this.questionNumber = questionNumber; }
        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
        public List<String> getOptions() { return options; }
        public void setOptions(List<String> options) { this.options = options; }
        public Integer getPoints() { return points; }
        public void setPoints(Integer points) { this.points = points; }
        public Boolean getAnswered() { return answered; }
        public void setAnswered(Boolean answered) { this.answered = answered; }
        public Boolean getIsCorrect() { return isCorrect; }
        public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }
        public String getDifficulty() { return difficulty; }
        public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
        public String getHint() { return hint; }
        public void setHint(String hint) { this.hint = hint; }
        public String getExplanation() { return explanation; }
        public void setExplanation(String explanation) { this.explanation = explanation; }
        public Set<TagResponse> getTags() { return tags; }
        public void setTags(Set<TagResponse> tags) { this.tags = tags; }
        public Integer getCorrectAnswer() { return correctAnswer; }
        public void setCorrectAnswer(Integer correctAnswer) { this.correctAnswer = correctAnswer; }
        public Integer getUserAnswer() { return userAnswer; }
        public void setUserAnswer(Integer userAnswer) { this.userAnswer = userAnswer; }
        public String getCorrectAnswerText() { return correctAnswerText; }
        public void setCorrectAnswerText(String correctAnswerText) { this.correctAnswerText = correctAnswerText; }
        public String getUserAnswerText() { return userAnswerText; }
        public void setUserAnswerText(String userAnswerText) { this.userAnswerText = userAnswerText; }
    }

    static class TagResponse {
        private Long id;
        private String name;
        private String iconUrl;

        public TagResponse() {}

        public TagResponse(Long id, String name, String iconUrl) {
            this.id = id;
            this.name = name;
            this.iconUrl = iconUrl;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getIconUrl() { return iconUrl; }
        public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    }

    static class AnswerRequest {
        private Integer answer;

        public Integer getAnswer() { return answer; }
        public void setAnswer(Integer answer) { this.answer = answer; }
    }

    static class AnswerResponse {
        private boolean correct;
        private String message;

        public AnswerResponse() {}

        public AnswerResponse(boolean correct, String message) {
            this.correct = correct;
            this.message = message;
        }

        public boolean isCorrect() { return correct; }
        public void setCorrect(boolean correct) { this.correct = correct; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    static class LessonStats {
        private int totalQuestions;
        private int answeredQuestions;
        private int correctAnswers;
        private int totalPoints;
        private int progress;
        private int accuracy;
        public int getTotalQuestions() { return totalQuestions; }
        public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
        public int getAnsweredQuestions() { return answeredQuestions; }
        public void setAnsweredQuestions(int answeredQuestions) { this.answeredQuestions = answeredQuestions; }
        public int getCorrectAnswers() { return correctAnswers; }
        public void setCorrectAnswers(int correctAnswers) { this.correctAnswers = correctAnswers; }
        public int getTotalPoints() { return totalPoints; }
        public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }
        public int getProgress() { return progress; }
        public void setProgress(int progress) { this.progress = progress; }
        public int getAccuracy() { return accuracy; }
        public void setAccuracy(int accuracy) { this.accuracy = accuracy; }
    }
}
