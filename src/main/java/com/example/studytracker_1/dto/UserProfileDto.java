package com.example.studytracker_1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String avatarUrl;
    private String bio;
    private Integer totalPoints;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private int totalLessons;
    private int completedLessons;
    private double averageScore;
    private List<LessonSummaryDto> recentLessons;}
