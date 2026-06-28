

package com.example.studytracker_1.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception ex, RedirectAttributes redirectAttributes) {
        // Логируем ошибку
        System.err.println("❌ Ошибка: " + ex.getMessage());
        ex.printStackTrace();

        // Добавляем сообщение об ошибке
        redirectAttributes.addFlashAttribute("error", "Произошла ошибка: " + ex.getMessage());

        return "redirect:/lessons";
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex, RedirectAttributes redirectAttributes) {
        System.err.println("❌ RuntimeException: " + ex.getMessage());
        ex.printStackTrace();

        redirectAttributes.addFlashAttribute("error", "Ошибка: " + ex.getMessage());

        return "redirect:/lessons";
    }
}

