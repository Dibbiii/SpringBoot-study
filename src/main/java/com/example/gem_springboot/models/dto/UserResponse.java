package com.example.gem_springboot.models.dto;

import java.util.List;

import com.example.gem_springboot.models.entities.UserEntity;

public record UserResponse(List<UserEntity> users, long total) {}
