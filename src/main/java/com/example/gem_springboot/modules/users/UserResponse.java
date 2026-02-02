package com.example.gem_springboot.modules.users;

import java.util.List;

import com.example.gem_springboot.modules.internal.UserEntity;


public record UserResponse(List<UserEntity> users, long total) {}
