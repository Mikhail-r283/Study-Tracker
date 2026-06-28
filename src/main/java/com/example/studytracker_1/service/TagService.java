package com.example.studytracker_1.service;

import com.example.studytracker_1.entity.Tag;
import com.example.studytracker_1.model.Question;
import com.example.studytracker_1.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional
    public Tag findOrCreate(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя тега не может быть пустым");
        }

        String normalizedName = name.trim().toLowerCase();
        return tagRepository.findByName(normalizedName)
                .orElseGet(() -> {
                    Tag tag = Tag.builder()
                            .name(normalizedName)
                            .iconUrl(getIconForTag(normalizedName))
                            .build();
                    return tagRepository.save(tag);
                });
    }

    // ✅ Переименовал для консистентности с вызовом в сервисе
    @Transactional
    public Tag findOrCreateTag(String name) {
        return findOrCreate(name);
    }

    @Transactional
    public Set<Tag> findOrCreateTags(Set<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        if (tagNames != null && !tagNames.isEmpty()) {
            for (String name : tagNames) {
                tags.add(findOrCreate(name));
            }
        }
        return tags;
    }

    // ✅ Новый метод для добавления тега к вопросу
    @Transactional
    public void addTagToQuestion(Question question, String tagName) {
        Tag tag = findOrCreate(tagName);
        question.getTags().add(tag);
    }

    // ✅ Новый метод для добавления нескольких тегов к вопросу
    @Transactional
    public void addTagsToQuestion(Question question, Set<String> tagNames) {
        if (tagNames != null) {
            for (String name : tagNames) {
                addTagToQuestion(question, name);
            }
        }
    }

    // ✅ Новый метод для удаления тега из вопроса
    @Transactional
    public void removeTagFromQuestion(Question question, String tagName) {
        Tag tag = tagRepository.findByName(tagName.trim().toLowerCase()).orElse(null);
        if (tag != null) {
            question.getTags().remove(tag);
        }
    }

    // ✅ Новый метод для получения всех тегов вопроса
    public Set<Tag> getTagsForQuestion(Question question) {
        return question.getTags();
    }

    // ✅ Новый метод для получения вопросов по тегу
    public List<Question> getQuestionsByTag(String tagName) {
        Tag tag = tagRepository.findByName(tagName.trim().toLowerCase()).orElse(null);
        if (tag != null) {
            return tag.getQuestions().stream().collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    // ✅ Новый метод для получения популярных тегов
    public List<Tag> getPopularTags(int limit) {
        return tagRepository.findAll().stream()
                .sorted((t1, t2) -> Integer.compare(t2.getQuestions().size(), t1.getQuestions().size()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // ✅ Новый метод для удаления тега (если он не используется)
    @Transactional
    public void deleteTagIfUnused(String tagName) {
        Tag tag = tagRepository.findByName(tagName.trim().toLowerCase()).orElse(null);
        if (tag != null && tag.getQuestions().isEmpty()) {
            tagRepository.delete(tag);
        }
    }

    public String getIconForTag(String tagName) {
        Map<String, String> iconMap = new HashMap<>();
        iconMap.put("математика", "https://img.icons8.com/color/48/math.png");
        iconMap.put("физика", "https://img.icons8.com/color/48/physics.png");
        iconMap.put("программирование", "https://img.icons8.com/color/48/code.png");
        iconMap.put("java", "https://img.icons8.com/color/48/java-coffee-cup-logo.png");
        iconMap.put("python", "https://img.icons8.com/color/48/python.png");
        iconMap.put("английский", "https://img.icons8.com/color/48/english.png");
        iconMap.put("история", "https://img.icons8.com/color/48/history.png");
        iconMap.put("дизайн", "https://img.icons8.com/color/48/design.png");
        iconMap.put("музыка", "https://img.icons8.com/color/48/music.png");
        iconMap.put("спорт", "https://img.icons8.com/color/48/sport.png");
        iconMap.put("база данных", "https://img.icons8.com/color/48/database.png");
        iconMap.put("алгоритмы", "https://img.icons8.com/color/48/algorithm.png");
        iconMap.put("sql", "https://img.icons8.com/color/48/sql.png");
        iconMap.put("spring", "https://img.icons8.com/color/48/spring-logo.png");
        iconMap.put("docker", "https://img.icons8.com/color/48/docker.png");
        iconMap.put("git", "https://img.icons8.com/color/48/git.png");
        iconMap.put("ооп", "https://img.icons8.com/color/48/oop.png");
        iconMap.put("архитектура", "https://img.icons8.com/color/48/architecture.png");
        iconMap.put("паттерны", "https://img.icons8.com/color/48/design-pattern.png");
        iconMap.put("devops", "https://img.icons8.com/color/48/devops.png");
        iconMap.put("maven", "https://img.icons8.com/color/48/maven.png");
        iconMap.put("orm", "https://img.icons8.com/color/48/orm.png");
        iconMap.put("api", "https://img.icons8.com/color/48/api.png");
        iconMap.put("rest", "https://img.icons8.com/color/48/rest-api.png");
        iconMap.put("веб", "https://img.icons8.com/color/48/web.png");
        iconMap.put("vcs", "https://img.icons8.com/color/48/version-control.png");
        iconMap.put("наследование", "https://img.icons8.com/color/48/inheritance.png");
        iconMap.put("полиморфизм", "https://img.icons8.com/color/48/polymorphism.png");
        iconMap.put("инкапсуляция", "https://img.icons8.com/color/48/encapsulation.png");
        iconMap.put("ди", "https://img.icons8.com/color/48/dependency-injection.png");
        iconMap.put("mysql", "https://img.icons8.com/color/48/mysql.png");
        iconMap.put("аннотации", "https://img.icons8.com/color/48/annotation.png");
        iconMap.put("парадигмы", "https://img.icons8.com/color/48/paradigm.png");
        iconMap.put("команды", "https://img.icons8.com/color/48/command-line.png");
        iconMap.put("сборка", "https://img.icons8.com/color/48/build.png");
        iconMap.put("фреймворк", "https://img.icons8.com/color/48/framework.png");
        iconMap.put("базы данных", "https://img.icons8.com/color/48/database.png");
        iconMap.put("jpa", "https://img.icons8.com/color/48/jpa.png");

        return iconMap.getOrDefault(tagName.toLowerCase(), "https://img.icons8.com/color/48/book.png");
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    public Optional<Tag> getTagByName(String name) {
        if (name == null) return Optional.empty();
        return tagRepository.findByName(name.trim().toLowerCase());
    }

    // ✅ Новый метод: поиск по части имени
    public List<Tag> searchTags(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllTags();
        }
        return tagRepository.findByNameContainingIgnoreCase(query.trim());
    }
}