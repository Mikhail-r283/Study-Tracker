package com.example.studytracker_1.controller;

import com.example.studytracker_1.dto.UserProfileDto;
import com.example.studytracker_1.entity.Tag;
import com.example.studytracker_1.model.Lesson;
import com.example.studytracker_1.model.Question;
import com.example.studytracker_1.model.User;
import com.example.studytracker_1.repository.LessonRepository;
import com.example.studytracker_1.repository.QuestionRepository;
import com.example.studytracker_1.repository.UserRepository;
import com.example.studytracker_1.service.LessonGenerationService;
import com.example.studytracker_1.service.LessonService;
import com.example.studytracker_1.service.UserProfileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/lessons")
public class LessonPageController {

    private final LessonRepository lessonRepository;
    private final LessonGenerationService lessonGenerationService;
    private final UserRepository userRepository;
    private final LessonService lessonService;
    @Autowired
    private QuestionRepository questionRepository;
    private final UserProfileService userProfileService;

    public LessonPageController(LessonRepository lessonRepository, LessonGenerationService lessonGenerationService, UserRepository userRepository, LessonService lessonService, UserProfileService userProfileService) {
        this.lessonRepository = lessonRepository;
        this.lessonGenerationService = lessonGenerationService;
        this.userRepository = userRepository;
        this.lessonService = lessonService;
        this.userProfileService = userProfileService;
    }



    @GetMapping()
    public String lessonsPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElse(null);

            if (user != null) {
                List<Lesson> lessons = lessonRepository.findByUserId(user.getId());
                System.out.println("Found lessons: " + lessons.size()); // Лог для проверки
                model.addAttribute("lessons", lessons);
            } else {
                System.out.println("User not found");
                model.addAttribute("lessons", List.of());
            }
        } else {
            System.out.println("User not authenticated");
            model.addAttribute("lessons", List.of());
        }
        return "lessons"; // Должен соответствовать имени HTML файла
    }

    @GetMapping("/debug")
    public String debugLessons(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
            if (user != null) {
                List<Lesson> allLessons = lessonRepository.findAll();
                List<Lesson> userLessons = lessonRepository.findByUserId(user.getId());
                System.out.println("=== DEBUG ===");
                System.out.println("Все уроки в БД: " + allLessons.size());
                System.out.println("Уроки пользователя " + user.getUsername() + ": " + userLessons.size());
                for (Lesson l : allLessons) {
                    System.out.println("Урок ID=" + l.getId() + ", title=" + l.getTitle() +
                            ", user_id=" + (l.getUser() != null ? l.getUser().getId() : "NULL"));
                }
                model.addAttribute("lessons", userLessons);
            }
        }
        return "lessons";
    }


    @GetMapping("/{id}")
    public String lessonDetail(@PathVariable Long id,
                               @RequestParam(name = "question", defaultValue = "0") int questionIndex,
                               @RequestParam(name = "completed", required = false) String completedParam,
                               HttpSession session,
                               Model model) {
        Lesson lesson = lessonService.findById(id);

        // Получаем или создаём список вопросов для отображения
        List<Question> displayQuestions = getDisplayQuestions(lesson, session);

        if (questionIndex < 0) questionIndex = 0;
        if (questionIndex >= displayQuestions.size()) questionIndex = displayQuestions.size() - 1;

        boolean completed = "true".equals(completedParam);

        // Если это первый вопрос - очищаем прогресс
        Map<Long, Map<Integer, Boolean>> lessonProgress = (Map<Long, Map<Integer, Boolean>>) session.getAttribute("lessonProgress");
        if (questionIndex == 0 && lessonProgress != null) {
            lessonProgress.remove(id);
            session.setAttribute("lessonProgress", lessonProgress);
            if (lesson.getAnswered()) {
                lesson.setAnswered(false);
                lessonService.update(lesson);
            }
        }

        Question currentQuestion = displayQuestions.get(questionIndex);

        // Считаем прогресс на основе displayQuestions
        int totalQuestions = displayQuestions.size();
        int progressPercent = totalQuestions > 0 ? (questionIndex * 100 / totalQuestions) : 0;

        // Проверяем, отвечал ли пользователь
        boolean isAnswered = false;
        boolean isCorrectAnswer = false;
        int correctIndex = -1;

        lessonProgress = (Map<Long, Map<Integer, Boolean>>) session.getAttribute("lessonProgress");
        if (lessonProgress != null && lessonProgress.containsKey(id)) {
            Map<Integer, Boolean> questionProgress = lessonProgress.get(id);
            if (questionProgress.containsKey(questionIndex)) {
                isAnswered = true;
                isCorrectAnswer = questionProgress.get(questionIndex);
                if (isCorrectAnswer) {
                    correctIndex = currentQuestion.getCorrectAnswer() - 1;
                }
            }
        }

        model.addAttribute("lesson", lesson);
        model.addAttribute("currentQuestion", currentQuestion);
        model.addAttribute("currentQuestionIndex", questionIndex);
        model.addAttribute("totalQuestions", totalQuestions);
        model.addAttribute("progressPercent", progressPercent);
        model.addAttribute("isAnswered", isAnswered);
        model.addAttribute("isCorrectAnswer", isCorrectAnswer);
        model.addAttribute("correctIndex", correctIndex);
        model.addAttribute("completed", completed);

        return "lesson-detail";
    }

    // Вспомогательный метод
    private List<Question> getDisplayQuestions(Lesson lesson, HttpSession session) {
        // Проверяем, есть ли уже сохранённый список в сессии
        List<Question> displayQuestions = (List<Question>) session.getAttribute("displayQuestions_" + lesson.getId());

        if (displayQuestions == null) {
            // Берём все вопросы из урока
            List<Question> allQuestions = lesson.getQuestions();

            // Убираем дубликаты по ID вопроса
            Set<Long> seenIds = new HashSet<>();
            displayQuestions = allQuestions.stream()
                    .filter(q -> seenIds.add(q.getId())) // добавляем только уникальные
                    .collect(Collectors.toList());

            // ИЛИ если дубликаты имеют разные ID, берём первые 4
            // displayQuestions = allQuestions.size() > 4 ?
            //     allQuestions.subList(0, 4) : allQuestions;

            // Сохраняем в сессию
            session.setAttribute("displayQuestions_" + lesson.getId(), displayQuestions);
        }

        return displayQuestions;
    }

    @PostMapping("/{id}/answer")
    public String answerQuestion(@PathVariable Long id,
                                 @RequestParam("selectedAnswer") int selectedAnswer,
                                 @RequestParam("questionIndex") int questionIndex,
                                 HttpSession session) {

        Lesson lesson = lessonService.findById(id);
        Question currentQuestion = lesson.getQuestions().get(questionIndex);

        Question dbQuestion = questionRepository.findById(currentQuestion.getId()).orElse(null);
        if (dbQuestion != null && Boolean.TRUE.equals(dbQuestion.getAnswered())) {
            // Уже отвечали — пропускаем
            int nextQuestion = questionIndex + 1;
            if (nextQuestion >= lesson.getQuestions().size()) {
                return "redirect:/lessons/" + id + "/result";
            }
            return "redirect:/lessons/" + id + "?question=" + nextQuestion;
        }


        // Проверяем правильность ответа
        int correctAnswer = currentQuestion.getCorrectAnswer();
        boolean isCorrect = (selectedAnswer == currentQuestion.getCorrectAnswer());


        System.out.println("=== ДИАГНОСТИКА ===");
        System.out.println("correctAnswer = " + correctAnswer);
        System.out.println("selectedAnswer = " + selectedAnswer);
        System.out.println("isCorrect = (" + selectedAnswer + " == " + correctAnswer + ")");
        if (correctAnswer >= 1 && correctAnswer <= 4) {
            System.out.println("isCorrect = (" + selectedAnswer + " == " + (correctAnswer - 1) + ")");
        } else {
            System.out.println("isCorrect = (" + selectedAnswer + " == " + correctAnswer + ")");
        }
        System.out.println("====================");


        // Сохраняем только этот конкретный вопрос
        Optional<Question> questionOpt = questionRepository.findById(currentQuestion.getId());
        if (questionOpt.isPresent()) {
            Question questionToUpdate = questionOpt.get();
            questionToUpdate.setAnswered(true);
            questionToUpdate.setIsCorrect(isCorrect);
            questionRepository.save(questionToUpdate);
            // 🔍 ДОБАВЬ ЭТО
            System.out.println("СОХРАНЁН ВОПРОС id=" + questionToUpdate.getId()
                    + " | answered=" + questionToUpdate.getAnswered()
                    + " | isCorrect=" + questionToUpdate.getIsCorrect()
                    + " | selectedAnswer=" + selectedAnswer
                    + " | correctAnswer=" + correctAnswer);
        }


        // Если ответ правильный - добавляем очки
        if (isCorrect) {
            lesson.setPoints(lesson.getPoints() + 10);
            lessonService.update(lesson);
        }

        int nextQuestion = questionIndex + 1;

        if (nextQuestion >= lesson.getQuestions().size()) {
            lesson.setAnswered(true);
            lessonService.update(lesson);
            return "redirect:/lessons/" + id + "/result";
        }

        return "redirect:/lessons/" + id + "?question=" + nextQuestion;
    }

    @PostMapping("/{id}/reset")
    public String resetLesson(@PathVariable Long id) {
        lessonService.resetLessonProgress(id);
        return "redirect:/lessons/" + id;
    }




    @DeleteMapping("/{lessonId}")
    public ResponseEntity<?> deleteLesson(@PathVariable Long lessonId,
                                          @AuthenticationPrincipal User currentUser) {
        try {
            lessonService.deleteLesson(lessonId, currentUser.getId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка удаления: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/delete")
    @ResponseBody
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        System.out.println("=== DELETE REQUEST for lesson ID: " + id + " ===");

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            System.out.println("Current user: " + currentUser.getId());

            // 1. Проверяем, существует ли урок
            Optional<Lesson> lessonOpt = lessonRepository.findById(id);
            if (lessonOpt.isEmpty()) {
                System.out.println("Lesson not found");
                return ResponseEntity.notFound().build();
            }

            Lesson lesson = lessonOpt.get();
            System.out.println("Lesson found: " + lesson.getTitle() + ", user_id: " +
                    (lesson.getUser() != null ? lesson.getUser().getId() : "NULL"));

            // 2. Удаляем вопросы для этого урока
            System.out.println("Deleting questions...");
            lessonRepository.deleteQuestionsByLessonId(id);

            // 3. Удаляем связи lesson_tags
            System.out.println("Deleting lesson tags...");
            lessonRepository.deleteLessonTagsByLessonId(id);

            // 4. Удаляем прогресс пользователей для этого урока
            System.out.println("Deleting user progress...");
            lessonRepository.deleteUserProgressByLessonId(id);

            // 5. Удаляем сам урок
            System.out.println("Deleting lesson...");
            lessonRepository.deleteById(id);
            System.out.println("Lesson deleted successfully!");

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace(System.out);
            return ResponseEntity.status(500).body(null);
        }
    }



    // Вспомогательные методы
    private LessonController.LessonResponse convertToResponse(Lesson lesson) {
        LessonController.LessonResponse response = new LessonController.LessonResponse();
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
            Set<LessonController.TagResponse> uniqueTags = new HashSet<>();
            for (Question q : lesson.getQuestions()) {
                if (q.getTags() != null) {
                    for (Tag tag : q.getTags()) {
                        uniqueTags.add(new LessonController.TagResponse(tag.getId(), tag.getName(), tag.getIconUrl()));
                    }
                }
            }
            response.setTags(new ArrayList<>(uniqueTags));
        }
        // Конвертируем вопросы
        if (lesson.getQuestions() != null) {
            List<LessonController.QuestionResponse> questionResponses = lesson.getQuestions().stream()
                    .map(q -> {
                        LessonController.QuestionResponse qr = new LessonController.QuestionResponse();
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
                                    .map(tag -> new LessonController.TagResponse(tag.getId(), tag.getName(), tag.getIconUrl()))
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

    // 4. Генерация урока через AI
    @PostMapping("/generate/AI")
    public String generateLesson(
            @RequestParam String topic,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        try {
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            Lesson lesson = lessonGenerationService.generateLesson(topic, user);

            redirectAttributes.addFlashAttribute("successMessage",
                    "✅ Урок '" + lesson.getTitle() + "' успешно создан!");

            return "redirect:/lessons";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage",
                    "❌ Ошибка: " + e.getMessage());
            return "redirect:/lessons";
        }
    }

  @GetMapping("/{id}/result")
        public String lessonResult(@PathVariable Long id, Model model) {
            Lesson lesson = lessonService.findById(id);
            List<Question> questions = questionRepository.findByLessonId(id);

            long totalQuestions = questions.size();

            // ✅ Правильные — у кого isCorrect = true
            long correctAnswers = questions.stream()
                    .filter(q -> Boolean.TRUE.equals(q.getIsCorrect()))
                    .count();

            // ✅ Неправильные — отвеченные, но НЕ правильные
            long answeredQuestions = questions.stream()
                    .filter(q -> Boolean.TRUE.equals(q.getAnswered()))
                    .count();

            long incorrectAnswers = answeredQuestions - correctAnswers;

            // Прогресс
            int progress = 0;
            if (totalQuestions > 0) {
                progress = (int) Math.round((correctAnswers * 100.0) / totalQuestions);
            }

            model.addAttribute("lesson", lesson);
            model.addAttribute("points", lesson.getPoints());
            model.addAttribute("progress", progress);
            model.addAttribute("correctAnswers", correctAnswers);
            model.addAttribute("incorrectAnswers", incorrectAnswers);
            model.addAttribute("totalQuestions", totalQuestions);

            // 🔍 ДИАГНОСТИКА — посмотри в консоли
            System.out.println("=== ДИАГНОСТИКА ===");
            System.out.println("Всего вопросов: " + totalQuestions);
            System.out.println("Отвечено: " + answeredQuestions);
            System.out.println("Правильных: " + correctAnswers);
            System.out.println("Неправильных: " + incorrectAnswers);
            for (Question q : questions) {
                System.out.println("  Вопрос " + q.getId()
                        + " | answered=" + q.getAnswered()
                        + " | isCorrect=" + q.getIsCorrect());
            }
            System.out.println("===================");

            return "lesson-result";
        }

    @PostMapping("/generate/random")
    public String generateRandomLesson(Principal principal, RedirectAttributes redirectAttributes) {
        System.out.println("=== generateRandomLesson START ===");
        System.out.println("Principal: " + principal);
        System.out.println("Principal.getName(): " + (principal != null ? principal.getName() : "NULL"));

        try {
            // 1. Проверяем пользователя
            if (principal == null || principal.getName() == null) {
                System.out.println("Пользователь не авторизован!");
                redirectAttributes.addFlashAttribute("errorMessage", "Пользователь не авторизован");
                return "redirect:/login";
            }

            // 2. Получаем пользователя
            Optional<User> currentUser = userRepository.findByUsername(principal.getName());
            System.out.println("User найден: " + currentUser.isPresent());

            if (currentUser.isEmpty()) {
                System.out.println("Пользователь не найден в БД!");
                redirectAttributes.addFlashAttribute("errorMessage", "Пользователь не найден");
                return "redirect:/lessons";
            }

            User user = currentUser.get();
            System.out.println("Генерируем урок для пользователя: " + user.getUsername() + ", ID: " + user.getId());

            // 3. Генерируем случайный урок
            Lesson lesson = lessonService.generateRandomLesson(user);
            System.out.println("Урок создан: " + lesson.getTitle() + ", ID: " + lesson.getId());

            redirectAttributes.addFlashAttribute("successMessage", "Случайный урок создан!");

        } catch (Exception e) {
            System.out.println("ОШИБКА: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка: " + e.getMessage());
        }

        System.out.println("=== generateRandomLesson END ===");
        return "redirect:/lessons";
    }

}