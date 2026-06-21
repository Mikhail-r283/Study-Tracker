package com.example.studytracker_1.config;

import com.example.studytracker_1.model.Lesson;
import com.example.studytracker_1.model.Statistics;
import com.example.studytracker_1.repository.LessonRepository;
import com.example.studytracker_1.repository.StatisticsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final LessonRepository lessonRepository;
    private final StatisticsRepository statisticsRepository;

    public DataInitializer(LessonRepository lessonRepository,
                           StatisticsRepository statisticsRepository) {
        this.lessonRepository = lessonRepository;
        this.statisticsRepository = statisticsRepository;
    }
    @Override
    public void run(String... args) {
         if (lessonRepository.count() == 0) {
            lessonRepository.save(new Lesson("Что такое Java?",
                    "Язык программирования", "База данных", "Операционная система", "Фреймворк", 1));
            lessonRepository.save(new Lesson("Что такое Spring Boot?",
                    "Игра", "Фреймворк для Java", "База данных", "Язык", 2));
            lessonRepository.save(new Lesson("Что такое MVC?",
                    "Model View Controller", "Model View Component", "Module View Controller", "Model Version Control", 1));
            lessonRepository.save(new Lesson("Какая аннотация используется для REST контроллера?",
                    "@Controller", "@RestController", "@Service", "@Component", 2));
            lessonRepository.save(new Lesson("Что такое JPA?",
                    "Java Platform API", "Java Persistence API", "Java Programming API", "Java Process API", 2));
        }



        if (statisticsRepository.count() == 0) {
            Statistics stats = new Statistics();
            stats.setTotalLessons((int) lessonRepository.count());
            statisticsRepository.save(stats);
        }
    }
}