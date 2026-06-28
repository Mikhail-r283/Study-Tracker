package com.example.studytracker_1.dto;

import com.example.studytracker_1.entity.Tag;
import com.example.studytracker_1.model.Difficulty;
import com.example.studytracker_1.service.LessonService;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
public class LessonTemplateDto {
    private final String title;
    private final String description;
    private final Difficulty difficulty;
    private final List<QuestionTemplateDto> questionTemplateDtos;
    private final Tag tag;

    public LessonTemplateDto(String title,
                             String description,
                             Difficulty difficulty,
                             List<QuestionTemplateDto> questionTemplateDtos,
                             Tag tag) {
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.questionTemplateDtos = questionTemplateDtos;
        this.tag = tag;
    }

    // Геттеры
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Difficulty getDifficulty() { return difficulty; }
    public List<QuestionTemplateDto> getQuestionTemplateDtos() { return questionTemplateDtos; }
    public Tag getTag() { return tag; }


}
