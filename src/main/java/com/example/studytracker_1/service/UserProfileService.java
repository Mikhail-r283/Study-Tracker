package com.example.studytracker_1.service;

import com.example.studytracker_1.dto.LessonSummaryDto;
import com.example.studytracker_1.dto.UserProfileDto;
import com.example.studytracker_1.model.Lesson;
import com.example.studytracker_1.model.User;
import com.example.studytracker_1.repository.LessonRepository;
import com.example.studytracker_1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserProfileService {

    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;

    public UserProfileService(UserRepository userRepository, LessonRepository lessonRepository) {
        this.userRepository = userRepository;
        this.lessonRepository = lessonRepository;
    }

    public UserProfileDto getProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName() != null ? user.getFullName() : username);
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());

        // Просто список уроков
        List<Lesson> lessons = lessonRepository.findByUserId(user.getId());
        List<LessonSummaryDto> lessonDtos = new ArrayList<>();

        for (Lesson lesson : lessons) {
            LessonSummaryDto lessonDto = new LessonSummaryDto();
            lessonDto.setId(lesson.getId());
            lessonDto.setTitle(lesson.getTitle());
            lessonDto.setAnswered(lesson.getAnswered());
            lessonDto.setPoints(lesson.getPoints() != null ? lesson.getPoints() : 0);
            lessonDtos.add(lessonDto);
        }

        dto.setRecentLessons(lessonDtos);
        dto.setTotalLessons(lessonDtos.size());
        dto.setCompletedLessons((int) lessonDtos.stream().filter(LessonSummaryDto::isAnswered).count());
        dto.setTotalPoints(lessonDtos.stream().mapToInt(LessonSummaryDto::getPoints).sum());

        return dto;
    }
}
