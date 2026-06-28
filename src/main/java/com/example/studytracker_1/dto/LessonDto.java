package com.example.studytracker_1.dto;

import java.util.List;

import com.example.studytracker_1.service.LessonGenerationService;
import lombok.Data;

@Data
public class LessonDto {
    private String title;
    private String description;
    private String difficulty; // EASY, MEDIUM, HARD
    private List<QuestionDto> questions;
}