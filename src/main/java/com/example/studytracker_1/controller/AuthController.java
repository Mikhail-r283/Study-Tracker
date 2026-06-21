package com.example.studytracker_1.controller;

import com.example.studytracker_1.model.Role;
import com.example.studytracker_1.model.User;
import com.example.studytracker_1.repository.RoleRepository;
import com.example.studytracker_1.repository.UserRepository;
import com.example.studytracker_1.service.LessonTemplateService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LessonTemplateService lessonTemplateService;


    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid UserDto userDto,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "register";
        }

        if (userRepository.existsByUsername(userDto.getUsername())) {
            model.addAttribute("usernameError", "Имя пользователя уже занято");
            return "register";
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            model.addAttribute("emailError", "Email уже используется");
            return "register";
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role newRole = new Role("USER");
                    return roleRepository.save(newRole);
                });
        user.setRoles(Set.of(userRole));

        userRepository.save(user);

        lessonTemplateService.createDefaultLessonsForUser(user);

        return "redirect:/login?registered";
    }

    // DTO класс внутри контроллера
    @Setter
    @Getter
    public static class UserDto {
        // Геттеры и сеттеры
        @NotBlank(message = "Имя пользователя обязательно")
        @Size(min = 3, max = 50, message = "От 3 до 50 символов")
        private String username;

        @NotBlank(message = "Email обязателен")
        @Email(message = "Некорректный email")
        private String email;

        @NotBlank(message = "Пароль обязателен")
        @Size(min = 6, message = "Минимум 6 символов")
        private String password;

    }
}
