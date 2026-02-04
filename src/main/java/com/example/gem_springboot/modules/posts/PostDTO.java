package com.example.gem_springboot.modules.posts;

// Questo record mappa i dati che arrivano dal servizio esterno (es. JSONPlaceholder)
public record PostDTO(
    Integer id,
    Integer userId,
    String title,
    String body
) {}