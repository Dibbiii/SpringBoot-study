package com.example.gem_springboot.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "course")
public record CourseProperties(
    String name,
    String welcomeMessage,
    int maxStudents,
    List<String> features,
    int difficultyLevel
) {}
