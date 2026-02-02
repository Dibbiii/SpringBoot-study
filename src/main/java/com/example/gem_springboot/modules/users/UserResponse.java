package com.example.gem_springboot.modules.users;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

public record UserResponse(
    Long id,
    String username,
    String email,
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss") // Spostiamo qui la formattazione!
    LocalDateTime createdAt
) {}