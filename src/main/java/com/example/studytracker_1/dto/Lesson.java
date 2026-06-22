package com.example.studytracker_1.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Lesson {
    private String question;
    private List<String> options;
    private int correctIndex; // 0-3
    private String description;
    private String difficulty; // EASY, MEDIUM, HARD

}