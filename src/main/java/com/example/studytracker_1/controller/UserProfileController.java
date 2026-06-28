package com.example.studytracker_1.controller;

import com.example.studytracker_1.dto.UserProfileDto;
import com.example.studytracker_1.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/profile")
    public String profilePage(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }

        String username = authentication.getName();

        try {
            UserProfileDto profile = userProfileService.getProfileByUsername(username);
            model.addAttribute("profile", profile);
            return "profile";
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            return "error";
        }
    }



//    @GetMapping
//    public ResponseEntity<UserProfileDto> getProfile(Authentication authentication) {
//        User user = (User) authentication.getPrincipal();
//        return ResponseEntity.ok(userProfileService.getProfile(user.getId()));
//    }
//
//    @PutMapping
//    public ResponseEntity<UserProfileDto> updateProfile(
//            Authentication authentication,
//            @RequestBody UserProfileDto updateDto) {
//        User user = (User) authentication.getPrincipal();
//        return ResponseEntity.ok(userProfileService.updateProfile(user.getId(), updateDto));
//    }
}