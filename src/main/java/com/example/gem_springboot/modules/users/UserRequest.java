package com.example.gem_springboot.modules.users;

// DTO
public record UserRequest(
    String username,
    String email,
    String password
) {}