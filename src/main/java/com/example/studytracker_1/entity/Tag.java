package com.example.studytracker_1.entity;

import com.example.studytracker_1.model.Lesson;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(length = 1000)
    private String description;

    @ManyToMany(mappedBy = "tags")
    private Set<Lesson> lessons;
}
