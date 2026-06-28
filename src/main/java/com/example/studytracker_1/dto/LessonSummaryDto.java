package com.example.studytracker_1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonSummaryDto {
    private Long id;
    private String title;
    private String difficulty;
    private int points;
    private int totalPoints;
    private boolean answered;
    private LocalDateTime createdAt;
}
