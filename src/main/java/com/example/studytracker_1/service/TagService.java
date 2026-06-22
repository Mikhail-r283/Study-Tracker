package com.example.studytracker_1.service;

import com.example.studytracker_1.entity.Tag;
import com.example.studytracker_1.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional
    public Tag findOrCreate(String name) {
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

    @Transactional
    public Set<Tag> findOrCreateTags(Set<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        if (tagNames != null) {
            for (String name : tagNames) {
                tags.add(findOrCreate(name));
            }
        }
        return tags;
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

        return iconMap.getOrDefault(tagName.toLowerCase(),
                "https://img.icons8.com/color/48/book.png");
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    public Optional<Tag> getTagByName(String name) {
        return tagRepository.findByName(name);
    }
}