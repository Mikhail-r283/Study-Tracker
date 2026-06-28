package com.example.studytracker_1.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class QuestionTemplateDto {
    private final String question;
    private final String option1;
    private final String option2;
    private final String option3;
    private final String option4;
    private final int correctAnswer;
    private final Integer points;

    public QuestionTemplateDto(String question, String option1, String option2, String option3, String option4, int correctAnswer, Integer points) {
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.correctAnswer = correctAnswer;
        this.points = points;
    }

    // Геттеры
    public String getQuestion() { return question; }
    public String getOption1() { return option1; }
    public String getOption2() { return option2; }
    public String getOption3() { return option3; }
    public String getOption4() { return option4; }
    public int getCorrectAnswer() { return correctAnswer; }
    public int getPoints() { return points; }

}
