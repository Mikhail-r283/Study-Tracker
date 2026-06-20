package com.example.studytracker_1.controller;

import com.example.studytracker_1.dto.AnswerRequest;
import com.example.studytracker_1.model.Lesson;
import com.example.studytracker_1.service.LessonService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/lessons")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }
    @GetMapping
    public String listLessons(Model model) {
        List<Lesson> lessons = lessonService.getAllLessons();
        model.addAttribute("lessons", lessons);
        return "lessons";
    }

    @GetMapping("/{id}")
    public String showLesson(@PathVariable Long id, Model model) {
        Lesson lesson = lessonService.getLessonById(id);
        model.addAttribute("lesson", lesson);
        model.addAttribute("answerRequest", new AnswerRequest());
        return "lesson-detail";
    }

    @PostMapping("/{id}/answer")
    public String answerLesson(@PathVariable Long id,
                               @ModelAttribute AnswerRequest answerRequest,
                               RedirectAttributes redirectAttributes) {
        boolean isCorrect = lessonService.checkAnswer(id, answerRequest.getSelectedAnswer());
        redirectAttributes.addFlashAttribute("isCorrect", isCorrect);
        return "redirect:/lessons/" + id + "/result";
    }

    @GetMapping("/{id}/result")
    public String showResult(@PathVariable Long id, Model model) {
        Lesson lesson = lessonService.getLessonById(id);
        model.addAttribute("lesson", lesson);
        return "result";
    }

    @PostMapping("/reset")
    public String resetLessons() {
        lessonService.resetLessons();
        return "redirect:/lessons";
    }
}