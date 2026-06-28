package com.example.studytracker_1.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuestionDto {
    private String question;
    private List<String> options;
    private int correctIndex;
    private String description;
}