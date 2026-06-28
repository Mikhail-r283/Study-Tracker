package com.example.studytracker_1.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProgressRequest {
    private int correctAnswers;
    private int totalQuestions;
    private int percentage;
    // геттеры/сеттеры
}
