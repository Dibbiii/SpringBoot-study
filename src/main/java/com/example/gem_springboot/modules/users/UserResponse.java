package com.example.gem_springboot.modules.users;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

// DTO di output -> è ciò che viene restituito fuori da springboot a chiunque esegua le api
public record UserResponse(
    Long id,
    String username,
    String email,
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss") // Spostiamo qui la formattazione!
    LocalDateTime createdAt
) {}
