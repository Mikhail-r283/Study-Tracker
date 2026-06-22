package com.example.studytracker_1.controller;

import com.example.studytracker_1.dto.AnswerRequest;
import com.example.studytracker_1.model.Lesson;
import com.example.studytracker_1.model.User;
import com.example.studytracker_1.repository.UserRepository;
import com.example.studytracker_1.service.AiService;
import com.example.studytracker_1.service.LessonGenerationService;
import com.example.studytracker_1.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import java.security.Principal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/lessons")
public class LessonController {

    private final LessonService lessonService;
    private final UserRepository userRepository;
    private final LessonGenerationService lessonGenerationService;


    public LessonController(LessonService lessonService, UserRepository userRepository, LessonGenerationService lessonGenerationService) {
        this.lessonService = lessonService;
        this.userRepository = userRepository;
        this.lessonGenerationService = lessonGenerationService;
    }

    @GetMapping("/{id}")
    public String showLesson(@PathVariable Long id, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        // ✅ ИСПРАВЛЕНО: получаем один урок по ID
        Lesson lesson = lessonService.getLessonById(id);

        if (lesson == null) {
            return "redirect:/lessons?error=notfound";
        }

        model.addAttribute("lesson", lesson);
        return "lesson-detail";
    }

    @GetMapping
    public String listLessons(Model model, Principal principal) {
        // Если пользователь не залогинен
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        // ✅ ИСПРАВЛЕНО: Получаем уроки только для этого пользователя
        List<Lesson> lessons = lessonService.getLessonsByUser(user);

        System.out.println("DEBUG: Найдено уроков для " + username + ": " + lessons.size());
        lessons.forEach(l -> System.out.println("  - " + l.getQuestion()));

        model.addAttribute("lessons", lessons);
        return "lessons";
    }

    @PostMapping("/{id}/answer")
    public String answerLesson(@PathVariable Long id,
                               @RequestParam("selectedAnswer") int selectedAnswer,
                               Principal principal) {

        System.out.println("===================");
        System.out.println("ID урока: " + id);

        Lesson lesson = lessonService.getLessonById(id);
        System.out.println("Вопрос: " + lesson.getQuestion());
        System.out.println("Пользователь выбрал: " + selectedAnswer);
        System.out.println("Правильный ответ (correctAnswer): " + lesson.getCorrectAnswer());
        System.out.println("option1: " + lesson.getOption1());
        System.out.println("option2: " + lesson.getOption2());

        boolean isCorrect = (selectedAnswer == lesson.getCorrectAnswer());
        System.out.println("Результат сравнения: " + isCorrect);
        System.out.println("===================");

        if (principal == null) {
            return "redirect:/login";
        }

        // Обновляем статус урока
        lesson.setAnswered(true);
        lesson.setIsCorrect(isCorrect);
        lessonService.saveLesson(lesson);

        // Перенаправляем на страницу результата
        return "redirect:/lessons/" + id + "/result?correct=" + isCorrect;
    }


    @GetMapping("/{id}/result")
    public String showResult(@PathVariable Long id,
                             @RequestParam("correct") boolean correct,
                             Model model,
                             Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        Lesson lesson = lessonService.getLessonById(id);
        if (lesson == null) {
            return "redirect:/lessons?error=notfound";
        }

        model.addAttribute("lesson", lesson);
        model.addAttribute("isCorrect", correct);
        return "result";
    }

    @GetMapping("/test-page")
    public String testPage(Model model, Principal principal) {
        System.out.println("=== /test controller called ===");

        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        // Используем метод, который фильтрует по пользователю!
        List<Lesson> lessons = lessonService.getLessonsByUser(user);
        System.out.println("Lessons count for user " + username + ": " + lessons.size());

        model.addAttribute("lessons", lessons);
        return "lessons";
    }
    @PostMapping("/generate")
    public String generateLesson(@RequestParam String question, Principal principal) {
        System.out.println("🔥🔥🔥 POST /lessons/generate ВЫЗВАН!");
        System.out.println("🔥🔥🔥 question = " + question);
        System.out.println("🔥🔥🔥 principal = " + principal);

        Optional<User> user = userRepository.findByUsername(principal.getName());
        System.out.println("🔥🔥🔥 user = " + user.orElse(null));

        Lesson lesson = lessonGenerationService.generateLesson(question, user.get());
        System.out.println("🔥🔥🔥 Урок создан, ID = " + lesson.getId());

        return "redirect:/lessons/" + lesson.getId();
    }
}


